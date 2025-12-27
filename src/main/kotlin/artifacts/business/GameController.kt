package artifacts.business

import artifacts.business.action.MoveResult
import artifacts.business.common.Loggers
import artifacts.business.util.Outcome

class GameController(
    private val game: Game,
    private val character: String,
) {

    fun run() {

        when (val result = game.move(character, Places.WORKSHOP_WEAPON_CRAFTING)) {
            is Outcome.Error -> logger.error("${result.javaClass}")
            is Outcome.Success -> when (result.value) {
                is MoveResult.AlreadyThere -> logger.info("$character is already there")
                is MoveResult.CharacterIsBusy -> logger.info("$character is busy")
                is MoveResult.CharacterIsInCooldown -> logger.info("$character is in cooldown")
                is MoveResult.ConditionsNotMet -> logger.info("$character does not match conditions")
                is MoveResult.MapIsBlocked -> logger.info("map is blocked")
                is MoveResult.Success -> logger.info("$character move done")
            }
        }
    }

    companion object {
        val logger = Loggers.getLogger(GameController::class.java)
    }
}