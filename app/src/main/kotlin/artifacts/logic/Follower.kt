package artifacts.logic

import artifacts.business.Behaviour
import artifacts.business.DefaultActions
import artifacts.business.Figure
import artifacts.business.GameCore
import artifacts.business.common.Cooldown
import artifacts.business.common.Name
import artifacts.business.util.Loggers

class Follower(
    private val core: GameCore,
    private val figure: Figure,
    private val target: Name,
) : Behaviour {

    override fun control(): Cooldown {
        val targetPos = core.figure(target).position
        if (figure.data().position != targetPos) {
            return DefaultActions.move(logger, figure, targetPos)
        }
        return Cooldown.NO_COOLDOWN
    }

    companion object {
        val logger = Loggers.getLogger(Follower::class.java)
    }
}
