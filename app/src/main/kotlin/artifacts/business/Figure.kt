package artifacts.business

import artifacts.business.result.*
import artifacts.business.common.Equipment
import artifacts.business.common.GameError
import artifacts.business.common.Item
import artifacts.business.common.Name
import artifacts.business.common.Position
import artifacts.business.common.Slot
import artifacts.business.common.Status
import artifacts.business.util.Outcome

class Figure(
    private val core: GameCore,
    val name: Name,
) {
    fun status(): Status = core.status(name)
    fun position(): Position = core.position(name)
    fun equipment(): Map<Slot, Equipment> = core.equipment(name)

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

    fun equip(item: Item, slot: Slot, quantity: Int): Outcome<EquipResult, GameError> =
        core.equip(name, item, slot, quantity)

    fun unequip(slot: Slot, quantity: Int): Outcome<UnequipResult, GameError> =
        core.unequip(name, slot, quantity)
}
