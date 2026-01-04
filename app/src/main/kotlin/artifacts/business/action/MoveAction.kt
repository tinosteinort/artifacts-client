package artifacts.business.action

import artifacts.business.Action
import artifacts.business.Game
import artifacts.business.common.Cooldown
import artifacts.business.common.Loggers
import artifacts.business.common.Position
import artifacts.business.util.Outcome

class MoveAction(
    private val place: () -> Position
) : Action {

    override fun execute(game: Game, figureName: String): Cooldown {

        when (val result = game.move(figureName, place())) {
            is Outcome.Error -> logger.error("${result.javaClass}")
            is Outcome.Success -> when (result.value) {
                is MoveResult.AlreadyThere -> {
                    logger.info("$figureName is already there")
                    return Cooldown.forSeconds(0)
                }
                is MoveResult.CharacterIsBusy -> logger.info("$figureName is busy")
                is MoveResult.CharacterIsInCooldown -> logger.info("$figureName is in cooldown")
                is MoveResult.ConditionsNotMet -> logger.info("$figureName does not match conditions")
                is MoveResult.MapIsBlocked -> logger.info("map is blocked")
                is MoveResult.Success -> {
                    logger.info("$figureName move done")
                    return result.value.cooldown
                }
            }
        }

        return Cooldown.forSeconds(5)
    }

    companion object {
        val logger = Loggers.getLogger(MoveAction::class.java)
    }
}
