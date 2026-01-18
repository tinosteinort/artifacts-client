package artifacts.business

import artifacts.business.action.*
import artifacts.business.common.GameError
import artifacts.business.common.Position
import artifacts.business.util.Outcome

interface GameCore {

    fun move(name: Name, position: Position): Outcome<MoveResult, GameError>
    fun fight(name: Name): Outcome<FightResult, GameError>
    fun rest(name: Name): Outcome<RestResult, GameError>
    fun gather(name: Name): Outcome<GatherResult, GameError>
    fun craft(name: Name, item: Item, quantity: Int): Outcome<CraftResult, GameError>
    fun equip(name: Name, item: Item, slot: Slot, quantity: Int = 1): Outcome<EquipResult, GameError>
    fun unequip(name: Name, slot: Slot, quantity: Int): Outcome<UnequipResult, GameError>
}
