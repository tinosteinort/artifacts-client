package artifacts.business

import artifacts.business.common.GameError
import artifacts.business.common.Position
import artifacts.business.util.Outcome

interface Game {

    fun move(character: String, position: Position): Outcome<MoveResult, GameError>
}
