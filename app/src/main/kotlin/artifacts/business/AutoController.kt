package artifacts.business

import artifacts.business.common.Cooldown
import artifacts.business.util.Loggers

class AutoController(
    val figure: Figure,
    var controller: Controller,
) {
    private var cooldown: Cooldown? = null
    private val logger = Loggers.getLogger(controller::class.java)

    fun control() {
        if (cooldown.inCooldown()) {
            return
        }
        cooldown = controller.control(figure)
        logger.cooldown(figure, cooldown!!)
    }

    private fun Cooldown?.inCooldown(): Boolean =
        this?.inCooldown() ?: false
}
