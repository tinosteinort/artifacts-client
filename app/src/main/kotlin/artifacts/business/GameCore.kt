package artifacts.business

import artifacts.business.common.*
import artifacts.business.result.*
import artifacts.business.util.Outcome

interface GameCore {

    fun initItems(page: Int, pageSize: Int): Outcome<InitItemsResult, GameError>
    fun init(name: Name): Outcome<InitResult, GameError>

    fun move(name: Name, position: Position): Outcome<MoveResult, GameError>
    fun fight(name: Name): Outcome<FightResult, GameError>
    fun rest(name: Name): Outcome<RestResult, GameError>
    fun gather(name: Name): Outcome<GatherResult, GameError>
    fun craft(name: Name, item: Item, quantity: Int): Outcome<CraftResult, GameError>
    fun equip(name: Name, item: Item, slot: Slot, quantity: Int = 1): Outcome<EquipResult, GameError>
    fun unequip(name: Name, slot: Slot, quantity: Int): Outcome<UnequipResult, GameError>
    fun useItem(name: Name, item: Item, quantity: Int): Outcome<UseItemResult, GameError>
    fun giveItems(name: Name, target: Name, items: Set<ItemPack<Item.Name>>): Outcome<GiveItemsResult, GameError>

    fun status(name: Name): Status
    fun position(name: Name): Position
    fun equipment(name: Name): Map<Slot, Equipment>
    fun inventory(name: Name): Inventory
}
