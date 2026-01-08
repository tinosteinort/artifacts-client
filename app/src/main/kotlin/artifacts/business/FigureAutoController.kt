package artifacts.business

import artifacts.business.action.CraftAction

class FigureAutoController(
    val figure: Figure,
    var autoControl: Boolean = false
) {

    fun control() {
        if (!autoControl || figure.isBusy()) {
            return
        }

        figure.setActions(
            //listOf(
            //    RestAction(),
            //    MoveAction {
            //        Places.CHICKEN
            //    },
            //    FightAction(),
            //    FightAction(),
            //)
            listOf(
                CraftAction(Items.COOKED_CHICKEN, 1)
            )
        )
    }
}
