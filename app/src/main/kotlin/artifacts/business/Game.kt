package artifacts.business

import artifacts.business.action.FightResult
import artifacts.business.action.MoveResult
import artifacts.business.action.RestResult
import artifacts.business.common.GameError
import artifacts.business.common.Position
import artifacts.business.util.Outcome

interface Game {

    fun move(character: String, position: Position): Outcome<MoveResult, GameError>
    fun fight(character: String): Outcome<FightResult, GameError>
    fun rest(character: String): Outcome<RestResult, GameError>
}
