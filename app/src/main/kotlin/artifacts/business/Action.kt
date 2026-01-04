package artifacts.business

import artifacts.business.common.Cooldown

interface Action {

    fun execute(game: Game, figureName: String) : Cooldown
}
