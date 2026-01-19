package artifacts.business

import artifacts.business.common.Cooldown
import artifacts.business.util.Loggers

class AutoController(
    val figure: Figure,
    var behaviour: Behaviour,
) {
    private var cooldown: Cooldown? = null
    private val logger = Loggers.getLogger(behaviour::class.java)

    fun control() {
        if (cooldown.inCooldown()) {
            return
        }
        cooldown = behaviour.control()
        logger.cooldown(figure, cooldown!!)
    }

    private fun Cooldown?.inCooldown(): Boolean =
        this?.inCooldown() ?: false
}
