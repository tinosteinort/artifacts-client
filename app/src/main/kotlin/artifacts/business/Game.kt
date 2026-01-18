package artifacts.business

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Game(private val core: GameCore) {

    private val executor: ExecutorService = Executors.newFixedThreadPool(1)
    private val figures: MutableMap<Name, Figure> = mutableMapOf()
    private val autoControllers: MutableMap<Name, AutoController> = mutableMapOf()

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

    fun registerFigure(name: Name) {
        executor.execute {
            figures[name] = Figure(core, name)
        }
    }

    fun autoControl(name: Name, controller: Controller) {
        executor.execute {
            autoControllers.remove(name)
            autoControllers[name] = AutoController(
                figure = figures[name]!!,
                controller = controller
            )
        }
    }

    fun control(name: Name, controller: (Figure) -> Unit) {
        executor.execute {
            controller(figures[name]!!)
        }
    }

    fun autoControlOff(name: Name) {
        executor.execute {
            autoControllers.remove(name)
        }
    }
}