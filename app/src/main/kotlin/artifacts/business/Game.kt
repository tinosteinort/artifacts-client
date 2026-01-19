package artifacts.business

import artifacts.business.common.Name
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

    fun autoControl(name: Name, factory: (core: GameCore, figure: Figure) -> Behaviour) {
        executor.execute {
            autoControllers.remove(name)

            val figure = figures[name]!!
            val behaviour = factory(core, figure)
            behaviour.init()

            autoControllers[name] = AutoController(
                figure = figure,
                behaviour = behaviour
            )
        }
    }

    fun autoControlOff(name: Name) {
        executor.execute {
            autoControllers.remove(name)
        }
    }

    fun control(name: Name, controller: (Figure) -> Unit) {
        executor.execute {
            controller(figures[name]!!)
        }
    }
}
