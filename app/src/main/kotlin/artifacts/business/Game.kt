package artifacts.business

import artifacts.business.common.Name
import artifacts.business.util.Loggers
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
            executor.execute(::run)
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
        running = true
        initFigures()

        executor.execute(::run)
    }

    private fun initFigures(): Unit =
        figures.putAll(
            core.figureNames()
                .map { name ->
                    logger.info("register $name")
                    name to Figure(
                        core = core,
                        name = name,
                    )
                }.toSet()
        )

    fun stop() {
        if (!running) {
            return
        }
        executor.execute {
            running = false
            executor.shutdown()
        }
    }

    fun autoControl(name: Name, factory: (core: GameCore, figure: Figure) -> Behaviour) {
        executor.execute {
            autoControllers.remove(name)

            val figure = figures[name]!!
            val behaviour = factory(core, figure)

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

    companion object {
        val logger = Loggers.getLogger(Game::class.java)
    }
}
