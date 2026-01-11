package artifacts.business

import artifacts.business.action.CraftAction

class FigureAutoController(
    val figure: Figure,
    var autoControl: Boolean = false,
    var controller: (Figure) -> Unit,
) {

    fun control() {
        if (!autoControl || figure.isBusy()) {
            return
        }

        controller(figure)
    }
}
