package artifacts.adapter.cli

import artifacts.business.Game
import artifacts.business.common.*
import artifacts.business.result.*
import artifacts.business.util.Loggers
import artifacts.business.util.Outcome
import artifacts.logic.Fighter
import artifacts.logic.Follower

class CliAdapter(private val game: Game) {

    private fun readCommand(): String {
        print("> ")
        return readln()
    }

    private fun readName(): Name {
        print("name > ")
        return Name(readln())
    }

    private fun readPosition(): Position {
        print("position > ")
        return readln()
            .split(",")
            .let {
                Position(
                    it[0].toInt(),
                    it[1].toInt(),
                )
            }
    }

    private fun readItem(): Item {
        print("item > ")
        return Item(readln())
    }

    private fun readSlot(): Slot {
        print("slot > ")
        return Slot.valueOf(readln())
    }

    private fun readQuantity(): Int {
        print("quantity > ")
        return readln().toInt()
    }

    fun run() {
        println("type name of figure to control")
        var name = readName()
        println("type 'exit' to quit")
        do {
            val line = readCommand().trim()

            when (line) {
                "exit" -> {
                    game.stop()
                    break
                }

                "figure" -> name = readName()
                "move" -> move(name)
                "fight" -> fight(name)
                "rest" -> rest(name)
                "gather" -> gather(name)
                "craft" -> craft(name)
                "equip" -> equip(name)
                "unequip" -> unequip(name)
                "use item" -> useItem(name)
                "give item" -> giveItem(name)
                "follow" -> {
                    val target = readName()
                    game.autoControl(name) { core, figure ->
                        Follower(core, figure, target)
                    }
                }

                "fighter" -> {
                    game.autoControl(name, ::Fighter)
                }

                "auto off" -> game.autoControlOff(name)
                else -> println("unknown command")
            }
        } while (game.running)
    }

    private fun move(name: Name) {
        val position = readPosition()
        game.control(name) { figure ->
            when (val result = figure.move(position)) {
                is Outcome.Error -> logger.error(result.value)
                is Outcome.Success -> when (result.value) {
                    is MoveResult.AlreadyThere -> logger.info("$name is already there")
                    is MoveResult.CharacterIsBusy -> logger.info("$name is busy")
                    is MoveResult.CharacterIsInCooldown -> logger.info("$name is in cooldown")
                    is MoveResult.ConditionsNotMet -> logger.info("$name does not match conditions")
                    is MoveResult.MapIsBlocked -> logger.info("map is blocked")
                    is MoveResult.Success -> logger.info("$name move done")
                }
            }
        }
    }

    private fun fight(name: Name) {
        game.control(name) { figure ->
            when (val result = figure.fight()) {
                is Outcome.Error -> logger.error(result.value)
                is Outcome.Success -> when (result.value) {
                    is FightResult.FightEnded -> {
                        if (result.value.win) {
                            logger.info("$name won the fight against ${result.value.opponent}")
                        } else {
                            logger.info("$name lost the fight against ${result.value.opponent}")
                        }
                    }

                    is FightResult.CharacterIsInCooldown -> logger.info("$name is in cooldown")
                    is FightResult.InventoryFull -> logger.info("inventory of $name is full")
                    is FightResult.NoMonsterOnMap -> logger.info("$name cannot fight, no monster on map")
                    is FightResult.OnlyBossMonsterCanBeFoughtByMultipleCharacters ->
                        logger.info("$name: only boss monster can be fought by multiple characters")
                }
            }
        }
    }

    private fun rest(name: Name) {
        game.control(name) { figure ->
            when (val result = figure.rest()) {
                is Outcome.Error -> logger.error(result.value)
                is Outcome.Success -> when (result.value) {
                    is RestResult.CharacterIsBusy -> logger.info("$name is busy")
                    is RestResult.CharacterIsInCooldown -> logger.info("$name is in cooldown")
                    is RestResult.Success -> logger.info("$name did a rest")
                }
            }
        }
    }

    private fun gather(name: Name) {
        game.control(name) { figure ->
            when (val result = figure.gather()) {
                is Outcome.Error -> logger.error(result.value)
                is Outcome.Success -> when (result.value) {
                    is GatherResult.CharacterIsBusy -> logger.info("$name is busy")
                    is GatherResult.CharacterIsInCooldown -> logger.info("$name is in cooldown")
                    is GatherResult.InventoryFull -> logger.info("inventory of $name is full")
                    is GatherResult.NoResourceOnMap -> logger.info("$name: no resource on map")
                    is GatherResult.SkillLevelTooLow -> logger.info("gathering skill level of $name too low")
                    is GatherResult.Success -> logger.info("$name gathered")
                }
            }
        }
    }

    private fun craft(name: Name) {
        val item = readItem()
        game.control(name) { figure ->
            when (val result = figure.craft(item, 1)) {
                is Outcome.Error -> logger.error(result.value)
                is Outcome.Success -> when (result.value) {
                    is CraftResult.CharacterIsBusy -> logger.info("$name is busy")
                    is CraftResult.CharacterIsInCooldown -> logger.info("$name is in cooldown")
                    is CraftResult.CraftNotFound -> logger.info("$name: craft not found")
                    is CraftResult.InventoryFull -> logger.info("inventory of $name is full")
                    is CraftResult.MissingRequiredItems -> logger.info("$name is missing items")
                    is CraftResult.SkillLevelTooLow -> logger.info("crafting skill level of $name too low")
                    is CraftResult.Success -> logger.info("$name crafted ${result.value.items.size} items")
                }
            }
        }
    }

    private fun equip(name: Name) {
        val item = readItem()
        val slot = readSlot()
        val quantity = readQuantity()
        game.control(name) { figure ->
            when (val result = figure.equip(item, slot, quantity)) {
                is Outcome.Error -> logger.error(result.value)
                is Outcome.Success -> when (result.value) {
                    is EquipResult.CharacterIsBusy -> logger.info("$name is busy")
                    is EquipResult.CharacterIsInCooldown -> logger.info("$name is in cooldown")
                    is EquipResult.ConditionsNotMet -> logger.info("$name does not match conditions")
                    is EquipResult.InventoryFull -> logger.info("inventory of $name is full")
                    is EquipResult.ItemIsAlreadyEquipped -> logger.info("$name is already epuiped")
                    is EquipResult.ItemNotFound -> logger.info("$name: item not found")
                    is EquipResult.MissingRequiredItems -> logger.info("$name is missing items")
                    is EquipResult.NotEnoughHp -> logger.info("$name has not enough Hp")
                    is EquipResult.SlotNotEmpty -> logger.info("$name: slot is not empty")
                    is EquipResult.Success -> logger.info("$name was equipeed")
                    is EquipResult.TooManyUtilities -> logger.info("$name: too many utilities")
                }
            }
        }
    }

    private fun unequip(name: Name) {
        val slot = readSlot()
        val quantity = readQuantity()
        game.control(name) { figure ->
            when (val result = figure.unequip(slot, quantity)) {
                is Outcome.Error -> logger.error(result.value)
                is Outcome.Success -> when (result.value) {
                    is UnequipResult.CharacterIsBusy -> logger.info("$name is busy")
                    is UnequipResult.CharacterIsInCooldown -> logger.info("$name is in cooldown")
                    is UnequipResult.InventoryFull -> logger.info("inventory of $name is full")
                    is UnequipResult.ItemNotFound -> logger.info("$name: item not found")
                    is UnequipResult.MissingRequiredItems -> logger.info("$name is missing items")
                    is UnequipResult.NotEnoughHp -> logger.info("$name has not enough Hp")
                    is UnequipResult.SlotNotEquipped -> logger.info("$name: slot is not equipped")
                    is UnequipResult.Success -> logger.info("$name unequipped $slot")
                }
            }
        }
    }

    private fun useItem(name: Name) {
        val item = readItem()
        val quantity = readQuantity()
        game.control(name) { figure ->
            when (val result = figure.useItem(item, quantity)) {
                is Outcome.Error -> logger.error(result.value)
                is Outcome.Success -> when (result.value) {
                    is UseItemResult.CharacterIsBusy -> logger.info("$name is busy")
                    is UseItemResult.CharacterIsInCooldown -> logger.info("$name is in cooldown")
                    is UseItemResult.ConditionsNotMet -> logger.info("$name does not match conditions")
                    is UseItemResult.ItemIsNotConsumable -> logger.info("$name: item '${result.value.item}' is not consumable")
                    is UseItemResult.ItemNotFound -> logger.info("$name: item not found")
                    is UseItemResult.MissingRequiredItems -> logger.info("$name is missing items")
                    is UseItemResult.Success -> logger.info("item used")
                }
            }
        }
    }

    private fun giveItem(name: Name) {
        val target = readName()
        val item = readItem()
        val quantity = readQuantity()
        game.control(name) { figure ->
            when (val result = figure.giveItems(target, setOf(ItemPack(item, quantity)))) {
                is Outcome.Error -> logger.error(result.value)
                is Outcome.Success -> when (result.value) {
                    is GiveItemsResult.CharacterIsBusy -> logger.info("$name is busy")
                    is GiveItemsResult.CharacterIsInCooldown -> logger.info("$name is in cooldown")
                    is GiveItemsResult.ItemNotFound -> logger.info("$name: item not found")
                    is GiveItemsResult.InventoryFull -> logger.info("inventory of $name is full")
                    is GiveItemsResult.MissingRequiredItems -> logger.info("$name is missing items")
                    is GiveItemsResult.Success -> logger.info("give item successful")
                }
            }
        }
    }

    companion object {
        val logger = Loggers.getLogger(CliAdapter::class.java)
    }
}
