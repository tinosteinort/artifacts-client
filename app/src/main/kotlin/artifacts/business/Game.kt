package artifacts.business

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Game(private val core: GameCore) {

    private val executor: ExecutorService = Executors.newFixedThreadPool(1)
    private val figures: MutableMap<String, Figure> = mutableMapOf()
    private val autoControllers: MutableMap<String, AutoController> = mutableMapOf()

    var running: Boolean = false
        private set

    private fun run() {
        autoControlFigures()

        Thread.sleep(1000)

        if (running) {
            executor.execute {
                run()
            }
        }
    }

    private fun autoControlFigures() {
        autoControllers.forEach { (_, autoController) ->
            autoController.control()
        }
    }

    fun start() {
        if (running) {
            return
        }
        executor.execute {
            running = true
            executor.execute {
                run()
            }
        }
    }

    fun stop() {
        if (!running) {
            return
        }
        executor.execute {
            running = false
            executor.shutdown()
        }
    }

    fun registerFigure(figureName: String) {
        executor.execute {
            figures[figureName] = Figure(core, figureName)
        }
    }

    fun autoControl(figureName: String, controller: Controller) {
        executor.execute {
            autoControllers.remove(figureName)
            autoControllers[figureName] = AutoController(
                figure = figures[figureName]!!,
                controller = controller
            )
        }
    }

    fun control(figureName: String, controller: (Figure) -> Unit) {
        executor.execute {
            controller(figures[figureName]!!)
        }
    }

    fun autoControlOff(figureName: String) {
        executor.execute {
            autoControllers.remove(figureName)
        }
    }
}