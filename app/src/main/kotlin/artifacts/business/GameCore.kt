package artifacts.business

import artifacts.business.action.*
import artifacts.business.common.GameError
import artifacts.business.common.Position
import artifacts.business.util.Outcome

interface GameCore {

    fun move(character: String, position: Position): Outcome<MoveResult, GameError>
    fun fight(character: String): Outcome<FightResult, GameError>
    fun rest(character: String): Outcome<RestResult, GameError>
    fun gather(character: String): Outcome<GatherResult, GameError>
    fun craft(character: String, item: String, quantity: Int): Outcome<CraftResult, GameError>
    fun equip(character: String, item: String, slot: String, quantity: Int = 1): Outcome<EquipResult, GameError>
    fun unequip(character: String, slot: String, quantity: Int): Outcome<UnequipResult, GameError>
}
