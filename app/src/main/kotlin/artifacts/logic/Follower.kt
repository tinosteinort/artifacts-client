package artifacts.logic

import artifacts.business.Behaviour
import artifacts.business.Figure
import artifacts.business.GameCore
import artifacts.business.common.Cooldown
import artifacts.business.common.Name
import artifacts.business.common.Position
import artifacts.business.result.MoveResult
import artifacts.business.util.Loggers
import artifacts.business.util.Outcome

class Follower(
    private val core: GameCore,
    private val figure: Figure,
    private val target: Name,
) : Behaviour {

    override fun init() = when (core.init(figure.name)) {
        is Outcome.Error -> throw RuntimeException("could not init ${figure.name}")
        is Outcome.Success -> {
            logger.info("init done for ${figure.name}")
        }
    }

    override fun control(): Cooldown {
        val targetPos = core.position(target)
        if (figure.position() != targetPos) {
            return move(targetPos)
        }
        return Cooldown.NO_COOLDOWN
    }

    private fun move(target: Position): Cooldown = when (val result = figure.move(target)) {
        is Outcome.Error -> {
            logger.error(result.value)
            Cooldown.NO_COOLDOWN
        }

        is Outcome.Success -> when (result.value) {
            is MoveResult.AlreadyThere -> {
                logger.info("${figure.name} is already there")
                Cooldown.NO_COOLDOWN
            }

            is MoveResult.CharacterIsBusy -> {
                logger.info("${figure.name} is busy")
                Cooldown.NO_COOLDOWN
            }

            is MoveResult.CharacterIsInCooldown -> {
                logger.info("${figure.name} is in cooldown")
                Cooldown.NO_COOLDOWN
            }

            is MoveResult.ConditionsNotMet -> {
                logger.info("${figure.name} does not match conditions")
                Cooldown.NO_COOLDOWN
            }

            is MoveResult.MapIsBlocked -> {
                logger.info("map is blocked")
                Cooldown.NO_COOLDOWN
            }

            is MoveResult.Success -> {
                logger.info("${figure.name} move done")
                result.value.cooldown
            }
        }
    }

    companion object {
        val logger = Loggers.getLogger(Follower::class.java)
    }
}
