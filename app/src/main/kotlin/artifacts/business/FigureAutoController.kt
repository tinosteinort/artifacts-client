package artifacts.business

import artifacts.business.action.FightAction
import artifacts.business.action.MoveAction

class FigureAutoController(
    val figure: Figure,
    var autoControl: Boolean = false
) {

    fun control() {
        if (!autoControl || figure.isBusy()) {
            return
        }

        figure.setActions(
            listOf(
                //MoveAction {
                //    Places.CHICKEN
                //},
                FightAction(),
            )
        )
    }
}
