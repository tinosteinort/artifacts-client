package artifacts.business

import artifacts.business.common.*
import artifacts.business.result.*
import artifacts.business.util.GameException
import artifacts.business.util.Outcome

class Figure(
    private val core: GameCore,
    val name: Name,
    private val figureStore: FigureStore,
) {
    fun status(): Status = figureStore[name].status
    fun inventory(): Inventory = figureStore[name].inventory
    fun position(): Position = figureStore[name].position
    fun equipment(): Map<Slot, Equipment> = figureStore[name].equipment

    fun move(position: Position): MoveResult =
        when (val result = core.move(name, position)) {
            is Outcome.Error -> throw GameException(result.value)
            is Outcome.Success -> result.value
        }

    fun fight(): FightResult =
        when (val result = core.fight(name)) {
            is Outcome.Error -> throw GameException(result.value)
            is Outcome.Success -> result.value
        }

    fun rest(): RestResult =
        when (val result = core.rest(name)) {
            is Outcome.Error -> throw GameException(result.value)
            is Outcome.Success -> result.value
        }

    fun gather(): GatherResult =
        when (val result = core.gather(name)) {
            is Outcome.Error -> throw GameException(result.value)
            is Outcome.Success -> result.value
        }

    fun craft(item: Item, quantity: Int): CraftResult =
        when (val result = core.craft(name, item, quantity)) {
            is Outcome.Error -> throw GameException(result.value)
            is Outcome.Success -> result.value
        }

    fun equip(item: Item, slot: Slot, quantity: Int): EquipResult =
        when (val result = core.equip(name, item, slot, quantity)) {
            is Outcome.Error -> throw GameException(result.value)
            is Outcome.Success -> result.value
        }

    fun unequip(slot: Slot, quantity: Int): UnequipResult =
        when (val result = core.unequip(name, slot, quantity)) {
            is Outcome.Error -> throw GameException(result.value)
            is Outcome.Success -> result.value
        }

    fun useItem(item: Item, quantity: Int): UseItemResult =
        when (val result = core.useItem(name, item, quantity)) {
            is Outcome.Error -> throw GameException(result.value)
            is Outcome.Success -> result.value
        }

    fun giveItems(target: Name, items: Set<ItemPack<Item.Name>>): GiveItemsResult =
        when (val result = core.giveItems(name, target, items)) {
            is Outcome.Error -> throw GameException(result.value)
            is Outcome.Success -> result.value
        }
}
