package artifacts.business

import artifacts.business.common.Cooldown

interface Action {

    fun execute(core: GameCore, figureName: String) : Cooldown
}
