package artifacts.business.action

import artifacts.business.Action
import artifacts.business.Game
import artifacts.business.common.Cooldown
import artifacts.business.util.Loggers
import artifacts.business.util.Outcome

class RestAction : Action {

    override fun execute(game: Game, figureName: String): Cooldown {
        when (val result = game.rest(figureName)) {
            is Outcome.Error -> logger.error("${result.value::class.java}")
            is Outcome.Success -> when (result.value) {
                is RestResult.CharacterIsBusy -> logger.info("$figureName is busy")
                is RestResult.CharacterIsInCooldown -> logger.info("$figureName is in cooldown")
                is RestResult.Success -> {
                    logger.info("$figureName did a rest")
                    return result.value.cooldown
                }
            }
        }

        return Cooldown.forSeconds(1)
    }

    companion object {
        val logger = Loggers.getLogger(RestAction::class.java)
    }
}