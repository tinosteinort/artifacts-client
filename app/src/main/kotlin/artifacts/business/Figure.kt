package artifacts.business

import artifacts.business.action.*
import artifacts.business.common.GameError
import artifacts.business.common.Position
import artifacts.business.util.Outcome

class Figure(
    private val core: GameCore,
    val name: String,
) {

    fun move(position: Position): Outcome<MoveResult, GameError> =
        core.move(name, position)

    fun fight(): Outcome<FightResult, GameError> =
        core.fight(name)

    fun rest(): Outcome<RestResult, GameError> =
        core.rest(name)

    fun gather(): Outcome<GatherResult, GameError> =
        core.gather(name)

    fun craft(item: Item, quantity: Int): Outcome<CraftResult, GameError> =
        core.craft(name, item, quantity)

    fun equip(item: Item, slot: String, quantity: Int): Outcome<EquipResult, GameError> =
        core.equip(name, item, slot, quantity)

    fun unequip(slot: String, quantity: Int): Outcome<UnequipResult, GameError> =
        core.unequip(name, slot, quantity)
}
