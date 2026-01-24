package artifacts.business

import artifacts.business.common.*
import artifacts.business.result.*
import artifacts.business.util.Logger

object DefaultActions {

    fun move(logger: Logger, figure: Figure, position: Position): Cooldown =
        when (val result = figure.move(position)) {
            is MoveResult.AlreadyThere -> {
                logger.info("${figure.name} is already there")
                Cooldown.NO_COOLDOWN
            }

            is MoveResult.CharacterIsBusy -> {
                logger.info("${figure.name} is busy")
                Cooldown.NO_COOLDOWN
            }

            is MoveResult.CharacterIsInCooldown -> {
                logger.info("${figure.name} is in cooldown")
                Cooldown.NO_COOLDOWN
            }

            is MoveResult.ConditionsNotMet -> {
                logger.info("${figure.name} does not match conditions")
                Cooldown.NO_COOLDOWN
            }

            is MoveResult.MapIsBlocked -> {
                logger.info("map is blocked")
                Cooldown.NO_COOLDOWN
            }

            is MoveResult.Success -> {
                logger.info("${figure.name} move done")
                result.cooldown
            }
        }

    fun fight(logger: Logger, figure: Figure): Cooldown =
        when (val result = figure.fight()) {
            is FightResult.FightEnded -> {
                if (result.win) {
                    logger.info("${figure.name} won the fight against ${result.opponent}")
                } else {
                    logger.info("${figure.name} lost the fight against ${result.opponent}")
                }
                result.cooldown
            }

            is FightResult.CharacterIsInCooldown -> {
                logger.info("${figure.name} is in cooldown")
                Cooldown.NO_COOLDOWN
            }

            is FightResult.InventoryFull -> {
                logger.info("inventory of ${figure.name} is full")
                Cooldown.NO_COOLDOWN
            }

            is FightResult.NoMonsterOnMap -> {
                logger.info("${figure.name} cannot fight, no monster on map")
                Cooldown.NO_COOLDOWN
            }

            is FightResult.OnlyBossMonsterCanBeFoughtByMultipleCharacters -> {
                logger.info("${figure.name}: only boss monster can be fought by multiple characters")
                Cooldown.NO_COOLDOWN
            }
        }

    fun rest(logger: Logger, figure: Figure): Cooldown =
        when (val result = figure.rest()) {
            is RestResult.CharacterIsBusy -> {
                logger.info("${figure.name} is busy")
                Cooldown.NO_COOLDOWN
            }

            is RestResult.CharacterIsInCooldown -> {
                logger.info("${figure.name} is in cooldown")
                Cooldown.NO_COOLDOWN
            }

            is RestResult.Success -> {
                logger.info("${figure.name} did a rest")
                result.cooldown
            }
        }

    fun useItem(logger: Logger, figure: Figure, item: Item, quantity: Int): Cooldown =
        when (val result = figure.useItem(item, quantity)) {
            is UseItemResult.CharacterIsBusy -> {
                logger.info("${figure.name} is busy")
                Cooldown.NO_COOLDOWN
            }

            is UseItemResult.CharacterIsInCooldown -> {
                logger.info("${figure.name} is in cooldown")
                Cooldown.NO_COOLDOWN
            }

            is UseItemResult.ConditionsNotMet -> {
                logger.info("${figure.name} does not match conditions")
                Cooldown.NO_COOLDOWN
            }

            is UseItemResult.ItemIsNotConsumable -> {
                logger.info("${figure.name}: item '$item' is not consumable")
                Cooldown.NO_COOLDOWN
            }

            is UseItemResult.ItemNotFound -> {
                logger.info("${figure.name}: item not found")
                Cooldown.NO_COOLDOWN
            }

            is UseItemResult.MissingRequiredItems -> {
                logger.info("${figure.name} is missing items")
                Cooldown.NO_COOLDOWN
            }

            is UseItemResult.Success -> {
                logger.info("${figure.name} used item $item")
                result.cooldown
            }
        }

    fun gather(logger: Logger, figure: Figure): Cooldown =
        when (val result = figure.gather()) {
            is GatherResult.CharacterIsBusy -> {
                logger.info("${figure.name} is busy")
                Cooldown.NO_COOLDOWN
            }

            is GatherResult.CharacterIsInCooldown -> {
                logger.info("${figure.name} is in cooldown")
                Cooldown.NO_COOLDOWN
            }

            is GatherResult.InventoryFull -> {
                logger.info("inventory of ${figure.name} is full")
                Cooldown.NO_COOLDOWN
            }

            is GatherResult.NoResourceOnMap -> {
                logger.info("${figure.name}: no resource on map")
                Cooldown.NO_COOLDOWN
            }

            is GatherResult.SkillLevelTooLow -> {
                logger.info("gathering skill level of ${figure.name} too low")
                Cooldown.NO_COOLDOWN
            }

            is GatherResult.Success -> {
                result.items.forEach { (item, quantity) ->
                    logger.info("${figure.name} gathered ${quantity}x $item")
                }
                result.cooldown
            }
        }

    fun craft(logger: Logger, figure: Figure, item: Item.Name, quantity: Int): Cooldown =
        when (val result = figure.craft(item, 1)) {
            is CraftResult.CharacterIsBusy -> {
                logger.info("${figure.name} is busy}")
                Cooldown.NO_COOLDOWN
            }

            is CraftResult.CharacterIsInCooldown -> {
                logger.info("${figure.name} is in cooldown}")
                Cooldown.NO_COOLDOWN
            }

            is CraftResult.CraftNotFound -> {
                logger.info("${figure.name}: craft not found}")
                Cooldown.NO_COOLDOWN
            }

            is CraftResult.InventoryFull -> {
                logger.info("inventory of ${figure.name} is full}")
                Cooldown.NO_COOLDOWN
            }

            is CraftResult.MissingRequiredItems -> {
                logger.info("${figure.name} is missing items}")
                Cooldown.NO_COOLDOWN
            }

            is CraftResult.SkillLevelTooLow -> {
                logger.info("crafting skill level of ${figure.name} too low}")
                Cooldown.NO_COOLDOWN
            }

            is CraftResult.Success -> {
                logger.info("${figure.name} crafted ${result.items.size} items}")
                result.cooldown
            }
        }

    fun equip(logger: Logger, figure: Figure, item: Item.Name, slot: Slot, quantity: Int): Cooldown =
        when (val result = figure.equip(item, slot, quantity)) {
            is EquipResult.CharacterIsBusy -> {
                logger.info("${figure.name} is busy")
                Cooldown.NO_COOLDOWN
            }

            is EquipResult.CharacterIsInCooldown -> {
                logger.info("${figure.name} is in cooldown")
                Cooldown.NO_COOLDOWN
            }

            is EquipResult.ConditionsNotMet -> {
                logger.info("${figure.name} does not match conditions")
                Cooldown.NO_COOLDOWN
            }

            is EquipResult.InventoryFull -> {
                logger.info("inventory of ${figure.name} is full")
                Cooldown.NO_COOLDOWN
            }

            is EquipResult.ItemIsAlreadyEquipped -> {
                logger.info("${figure.name} is already epuiped")
                Cooldown.NO_COOLDOWN
            }

            is EquipResult.ItemNotFound -> {
                logger.info("${figure.name}: item not found")
                Cooldown.NO_COOLDOWN
            }

            is EquipResult.MissingRequiredItems -> {
                logger.info("${figure.name} is missing items")
                Cooldown.NO_COOLDOWN
            }

            is EquipResult.NotEnoughHp -> {
                logger.info("${figure.name} has not enough Hp")
                Cooldown.NO_COOLDOWN
            }

            is EquipResult.SlotNotEmpty -> {
                logger.info("${figure.name}: slot is not empty")
                Cooldown.NO_COOLDOWN
            }

            is EquipResult.TooManyUtilities -> {
                logger.info("${figure.name}: too many utilities")
                Cooldown.NO_COOLDOWN
            }

            is EquipResult.Success -> {
                logger.info("${figure.name} was equipeed")
                result.cooldown
            }
        }

    fun unequip(logger: Logger, figure: Figure, slot: Slot, quantity: Int): Cooldown =
        when (val result = figure.unequip(slot, quantity)) {
            is UnequipResult.CharacterIsBusy -> {
                logger.info("${figure.name} is busy")
                Cooldown.NO_COOLDOWN
            }

            is UnequipResult.CharacterIsInCooldown -> {
                logger.info("${figure.name} is in cooldown")
                Cooldown.NO_COOLDOWN
            }

            is UnequipResult.InventoryFull -> {
                logger.info("inventory of ${figure.name} is full")
                Cooldown.NO_COOLDOWN
            }

            is UnequipResult.ItemNotFound -> {
                logger.info("${figure.name}: item not found")
                Cooldown.NO_COOLDOWN
            }

            is UnequipResult.MissingRequiredItems -> {
                logger.info("${figure.name} is missing items")
                Cooldown.NO_COOLDOWN
            }

            is UnequipResult.NotEnoughHp -> {
                logger.info("${figure.name} has not enough Hp")
                Cooldown.NO_COOLDOWN
            }

            is UnequipResult.SlotNotEquipped -> {
                logger.info("${figure.name}: slot is not equipped")
                Cooldown.NO_COOLDOWN
            }

            is UnequipResult.Success -> {
                logger.info("${figure.name} unequipped $slot")
                result.cooldown
            }
        }

    fun giveItem(logger: Logger, figure: Figure, target: Name, item: Item.Name, quantity: Int): Cooldown =
        when (val result = figure.giveItems(target, setOf(ItemPack(item, quantity)))) {
            is GiveItemsResult.CharacterIsBusy -> {
                logger.info("${figure.name} is busy")
                Cooldown.NO_COOLDOWN
            }

            is GiveItemsResult.CharacterIsInCooldown -> {
                logger.info("${figure.name} is in cooldown")
                Cooldown.NO_COOLDOWN
            }

            is GiveItemsResult.ItemNotFound -> {
                logger.info("${figure.name}: item not found")
                Cooldown.NO_COOLDOWN
            }

            is GiveItemsResult.InventoryFull -> {
                logger.info("inventory of ${figure.name} is full")
                Cooldown.NO_COOLDOWN
            }

            is GiveItemsResult.MissingRequiredItems -> {
                logger.info("${figure.name} is missing items")
                Cooldown.NO_COOLDOWN
            }

            is GiveItemsResult.Success -> {
                logger.info("give item successful")
                result.cooldown
            }
        }
}
