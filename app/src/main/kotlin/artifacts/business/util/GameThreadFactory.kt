package artifacts.business.util

import artifacts.business.Game
import java.util.concurrent.ThreadFactory

class GameThreadFactory(private val game: Game) : ThreadFactory {

    override fun newThread(r: Runnable): Thread {
        val thread = Thread(r)
        thread.name = "GameThread"
        thread.uncaughtExceptionHandler = ExceptionHandler(game)
        return thread
    }
}