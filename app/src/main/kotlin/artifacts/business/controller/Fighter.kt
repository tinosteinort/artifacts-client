package artifacts.business.controller

import artifacts.business.Controller
import artifacts.business.Figure
import artifacts.business.action.FightAction

class Fighter : Controller {

    override fun control(figure: Figure) {
        figure.setAction(FightAction())
    }
}