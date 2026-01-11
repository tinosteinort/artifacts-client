package artifacts.business

class Game(private val core: GameCore) {

    private val figures: MutableMap<String, Figure> = mutableMapOf()
    private val autoControllers: MutableMap<String, FigureAutoController> = mutableMapOf()

    var running: Boolean = true

    fun run() {
        while (running) {

            executeActionsOfFigure()
            autoControlFigures()

            Thread.sleep(1000)
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

    fun registerFigure(figureName: String) {
        figures[figureName] = Figure(core, figureName)
    }

    /**
     * controller will be called every time, when actions of figure are empty
     */
    fun autoControl(figureName: String, controller: (Figure) -> Unit) {
        autoControllers[figureName] = FigureAutoController(
            figure = figures[figureName]!!,
            autoControl = true,
            controller = controller
        )
    }
}