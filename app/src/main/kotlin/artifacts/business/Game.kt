package artifacts.business

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Game(private val core: GameCore) {

    private val executor: ExecutorService = Executors.newFixedThreadPool(1)
    private val figures: MutableMap<String, Figure> = mutableMapOf()
    private val autoControllers: MutableMap<String, FigureAutoController> = mutableMapOf()

    var running: Boolean = false
        private set

    private fun run() {
        executeActionsOfFigure()
        autoControlFigures()

        Thread.sleep(1000)

        if (running) {
            executor.execute {
                run()
            }
        }
    }

    private fun executeActionsOfFigure() {
        figures.forEach { (_, figure) ->
            figure.executeAction()
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

    /**
     * controller will be called every time, when actions of figure are empty
     */
    fun autoControl(figureName: String, controller: (Figure) -> Unit) {
        executor.execute {
            autoControllers.remove(figureName)
            autoControllers[figureName] = FigureAutoController(
                figure = figures[figureName]!!,
                autoControl = true,
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