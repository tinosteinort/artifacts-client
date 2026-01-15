package artifacts.business.util

import artifacts.business.Figure
import artifacts.business.common.Cooldown
import artifacts.business.common.GameError

class Logger(private val cls: Class<*>) {

    fun info(message: String) {
        println("Info[${cls.simpleName}]: $message")
    }

    fun error(error: GameError) {
        val message = when (error) {
            is GameError.CharacterNotFound -> "${error.javaClass.simpleName}"
            is GameError.MapNotFound -> "${error.javaClass.simpleName}"
            is GameError.NoPathAvailable -> "${error.javaClass.simpleName}"
            is GameError.Generic -> "Generic: ${error.message}"
        }
        println("Error[${cls.simpleName}]: $message")
    }

    fun cooldown(figure: Figure, cooldown: Cooldown) {
        println("Info[${cls.simpleName}]: Cooldown for ${figure.name}: ${cooldown.seconds}s")
    }
}
