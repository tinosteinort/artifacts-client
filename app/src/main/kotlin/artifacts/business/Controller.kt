package artifacts.business

import artifacts.business.common.Cooldown

interface Controller {

    fun control(figure: Figure) : Cooldown
}
