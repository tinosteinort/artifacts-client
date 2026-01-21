package artifacts.business

import artifacts.business.common.*
import artifacts.business.result.*
import artifacts.business.util.Outcome

class Figure(
    private val core: GameCore,
    val name: Name,
) {
    fun status(): Status = core.status(name)
    fun position(): Position = core.position(name)
    fun equipment(): Map<Slot, Equipment> = core.equipment(name)
    fun inventory(): Inventory = core.inventory(name)

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

    fun useItem(item: Item, quantity: Int): Outcome<UseItemResult, GameError> =
        core.useItem(name, item, quantity)

    fun giveItems(target: Name, items: Set<ItemPack<Item.Name>>): Outcome<GiveItemsResult, GameError> =
        core.giveItems(name, target, items)
}
