package artifacts.business.util

import artifacts.business.Game

class ExceptionHandler(private val game: Game) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        when (e) {
            is GameException -> {
                logger.error(e.error)
            }
            else -> logger.error("${e::class.java}: ${e.message}")
        }
        game.stop()
    }

    companion object {
        val logger = Loggers.getLogger(ExceptionHandler::class.java)
    }
}