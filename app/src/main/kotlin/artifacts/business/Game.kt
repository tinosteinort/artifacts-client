package artifacts.business

import artifacts.business.common.Item
import artifacts.business.common.Name
import artifacts.business.result.GetFiguresResult
import artifacts.business.result.GetItemsResult
import artifacts.business.util.GameException
import artifacts.business.util.Loggers
import artifacts.business.util.Outcome
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Game(private val core: GameCore) {

    private val executor: ExecutorService = Executors.newFixedThreadPool(1)
    private val figures: MutableMap<Name, Figure> = mutableMapOf()
    private val autoControllers: MutableMap<Name, AutoController> = mutableMapOf()
    private val items: MutableMap<Item.Name, Item.Details> = mutableMapOf()
    private val figureStore: FigureStore = FigureStore(items)

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
            initItems()
            initFigures()
            executor.execute {
                run()
            }
        }
    }

    private fun initFigures() {
        logger.info("load figures")
        when (val result = core.getFigures()) {
            is Outcome.Error -> throw GameException(result.value)
            is Outcome.Success -> when (result.value) {
                is GetFiguresResult.Success -> {
                    result.value.figures.forEach { figureData ->
                        logger.info("register ${figureData.name}")
                        figureStore[figureData.name] = figureData
                        figures[figureData.name] = Figure(
                            core = core,
                            name = figureData.name,
                            figureStore = figureStore,
                        )
                    }
                }
            }
        }
    }

    private fun initItems() {
        logger.info("load items")
        var page = 1
        var pages: Int?

        do {
            when (val result = core.getItems(page, 100)) {
                is Outcome.Error -> throw GameException(result.value)
                is Outcome.Success -> when (result.value) {
                    is GetItemsResult.Success -> {
                        items.putAll(result.value.items)
                        page = result.value.page + 1
                        pages = result.value.pages
                    }
                }
            }
        } while (page != pages + 1)
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

    fun autoControl(name: Name, factory: (figureStore: FigureStore, figure: Figure) -> Behaviour) {
        executor.execute {
            autoControllers.remove(name)

            val figure = figures[name]!!
            val behaviour = factory(figureStore, figure)

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
