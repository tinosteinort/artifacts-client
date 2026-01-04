package artifacts.business

import artifacts.business.action.FightResult
import artifacts.business.common.Loggers
import artifacts.business.util.Outcome

class GameController(
    private val game: Game,
    private val character: String,
) {

    fun run() {

        //when (val result = game.move(character, Places.CHICKEN)) {
        //    is Outcome.Error -> logger.error("${result.javaClass}")
        //    is Outcome.Success -> when (result.value) {
        //        is MoveResult.AlreadyThere -> logger.info("$character is already there")
        //        is MoveResult.CharacterIsBusy -> logger.info("$character is busy")
        //        is MoveResult.CharacterIsInCooldown -> logger.info("$character is in cooldown")
        //        is MoveResult.ConditionsNotMet -> logger.info("$character does not match conditions")
        //        is MoveResult.MapIsBlocked -> logger.info("map is blocked")
        //        is MoveResult.Success -> logger.info("$character move done")
        //    }
        //}

        when (val result = game.fight(character)) {
            is Outcome.Error -> logger.error("${result.value::class.java}")
            is Outcome.Success -> when (result.value) {
                is FightResult.FightEnded -> {
                    logger.info("fight ended")
                }
                is FightResult.CharacterIsInCooldown -> logger.info("$character is in cooldown")
                is FightResult.InventoryFull -> logger.info("inventory of $character is full")
                is FightResult.NoMonsterOnMap -> logger.info("$character cannot fight, no monster on map")
                is FightResult.OnlyBossMonsterCanBeFoughtByMultipleCharacters ->
                    logger.info("$character: only boss monster can be fought by multiple characters")
            }
        }
    }

    companion object {
        val logger = Loggers.getLogger(GameController::class.java)
    }
}