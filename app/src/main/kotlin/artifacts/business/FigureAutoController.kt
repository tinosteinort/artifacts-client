package artifacts.business

import artifacts.business.action.FightAction
import artifacts.business.action.MoveAction
import artifacts.business.action.RestAction

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
                RestAction(),
                MoveAction {
                    Places.CHICKEN
                },
                FightAction(),
                FightAction(),
            )
        )
    }
}
