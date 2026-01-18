package artifacts.adapter.cli

import artifacts.business.Game
import artifacts.business.action.*
import artifacts.business.common.Position
import artifacts.logic.Fighter
import artifacts.business.util.Loggers
import artifacts.business.util.Outcome

class CliAdapter(private val game: Game) {

    private fun readCommand(): String? {
        print("> ")
        return readlnOrNull()
    }

    private fun readName(): String {
        print("name > ")
        return readln()
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

    private fun readItem(): String {
        print("item > ")
        return readln()
    }

    private fun readSlot(): String {
        print("slot > ")
        return readln()
    }

    private fun readQuantity(): Int {
        print("quantity > ")
        return readln().toInt()
    }

    fun run() {
        println("type name of figure to control")
        var figureName = readName()
        println("type 'exit' to quit")
        do {
            val line = readCommand()

            when (line) {
                "exit" -> {
                    game.stop()
                    break
                }

                "figure" -> {
                    figureName = readName()
                }

                "move" -> {
                    move(figureName)
                }

                "fight" -> {
                    fight(figureName)
                }

                "rest" -> {
                    rest(figureName)
                }

                "gather" -> {
                    gather(figureName)
                }

                "craft" -> {
                    craft(figureName)
                }

                "equip" -> {
                    equip(figureName)
                }

                "unequip" -> {
                    unequip(figureName)
                }

                "auto on" -> {
                    game.autoControl(figureName, Fighter())
                }

                "auto off" -> {
                    game.autoControlOff(figureName)
                }

                else -> {
                    println("unknown command")
                }
            }
        } while (game.running)
    }

    private fun move(figureName: String) {
        val position = readPosition()
        game.control(figureName) { figure ->
            when (val result = figure.move(position)) {
                is Outcome.Error -> logger.error(result.value)
                is Outcome.Success -> when (result.value) {
                    is MoveResult.AlreadyThere -> {
                        logger.info("$figureName is already there")
                    }

                    is MoveResult.CharacterIsBusy -> logger.info("$figureName is busy")
                    is MoveResult.CharacterIsInCooldown -> logger.info("$figureName is in cooldown")
                    is MoveResult.ConditionsNotMet -> logger.info("$figureName does not match conditions")
                    is MoveResult.MapIsBlocked -> logger.info("map is blocked")
                    is MoveResult.Success -> {
                        logger.info("$figureName move done")
                    }
                }
            }
        }
    }

    private fun fight(figureName: String) {
        game.control(figureName) { figure ->
            when (val result = figure.fight()) {
                is Outcome.Error -> logger.error(result.value)
                is Outcome.Success -> when (result.value) {
                    is FightResult.FightEnded -> {
                        if (result.value.win) {
                            logger.info("$figureName won the fight against ${result.value.opponent}")
                        } else {
                            logger.info("$figureName lost the fight against ${result.value.opponent}")
                        }
                    }

                    is FightResult.CharacterIsInCooldown -> logger.info("$figureName is in cooldown")
                    is FightResult.InventoryFull -> logger.info("inventory of $figureName is full")
                    is FightResult.NoMonsterOnMap -> logger.info("$figureName cannot fight, no monster on map")
                    is FightResult.OnlyBossMonsterCanBeFoughtByMultipleCharacters ->
                        logger.info("$figureName: only boss monster can be fought by multiple characters")
                }
            }
        }
    }

    private fun rest(figureName: String) {
        game.control(figureName) { figure ->
            when (val result = figure.rest()) {
                is Outcome.Error -> logger.error(result.value)
                is Outcome.Success -> when (result.value) {
                    is RestResult.CharacterIsBusy -> logger.info("$figureName is busy")
                    is RestResult.CharacterIsInCooldown -> logger.info("$figureName is in cooldown")
                    is RestResult.Success -> {
                        logger.info("$figureName did a rest")
                    }
                }
            }
        }
    }

    private fun gather(figureName: String) {
        game.control(figureName) { figure ->
            when (val result = figure.gather()) {
                is Outcome.Error -> logger.error(result.value)
                is Outcome.Success -> when (result.value) {
                    is GatherResult.CharacterIsBusy -> logger.info("$figureName is busy")
                    is GatherResult.CharacterIsInCooldown -> logger.info("$figureName is in cooldown")
                    is GatherResult.InventoryFull -> logger.info("inventory of $figureName is full")
                    is GatherResult.NoResourceOnMap -> logger.info("$figureName: no resource on map")
                    is GatherResult.SkillLevelTooLow -> logger.info("gathering skill level of $figureName too low")
                    is GatherResult.Success -> {
                        logger.info("$figureName gathered")
                    }
                }
            }
        }
    }

    private fun craft(figureName: String) {
        val item = readItem()
        game.control(figureName) { figure ->
            when (val result = figure.craft(item, 1)) {
                is Outcome.Error -> logger.error(result.value)
                is Outcome.Success -> when (result.value) {
                    is CraftResult.CharacterIsBusy -> logger.info("$figureName is busy")
                    is CraftResult.CharacterIsInCooldown -> logger.info("$figureName is in cooldown")
                    is CraftResult.CraftNotFound -> logger.info("$figureName: craft not found")
                    is CraftResult.InventoryFull -> logger.info("inventory of $figureName is full")
                    is CraftResult.MissingRequiredItems -> logger.info("$figureName is missing items")
                    is CraftResult.SkillLevelTooLow -> logger.info("crafting skill level of $figureName too low")
                    is CraftResult.Success -> {
                        logger.info("$figureName crafted ${result.value.items.size} items")
                    }
                }
            }
        }
    }

    private fun equip(figureName: String) {
        val item = readItem()
        val slot = readSlot()
        val quantity = readQuantity()
        game.control(figureName) { figure ->
            when (val result = figure.equip(item, slot, quantity)) {
                is Outcome.Error -> logger.error(result.value)
                is Outcome.Success -> when (result.value) {
                    is EquipResult.CharacterIsBusy -> logger.info("$figureName is busy")
                    is EquipResult.CharacterIsInCooldown -> logger.info("$figureName is in cooldown")
                    is EquipResult.ConditionsNotMet -> logger.info("$figureName does not match conditions")
                    is EquipResult.InventoryFull -> logger.info("inventory of $figureName is full")
                    is EquipResult.ItemIsAlreadyEquipped -> logger.info("$figureName is already epuiped")
                    is EquipResult.ItemNotFound -> logger.info("$figureName: item not found")
                    is EquipResult.MissingRequiredItems -> logger.info("$figureName is missing items")
                    is EquipResult.NotEnoughHp -> logger.info("$figureName has not enough Hp")
                    is EquipResult.SlotNotEmpty -> logger.info("$figureName: slot is not empty")
                    is EquipResult.Success -> logger.info("$figureName was equipeed")
                    is EquipResult.TooManyUtilities -> logger.info("$figureName: too many utilities")
                }
            }
        }
    }

    private fun unequip(figureName: String) {
        game.control(figureName) { figure ->
            val slot = readSlot()
            val quantity = readQuantity()
            when (val result = figure.unequip(slot, quantity)) {
                is Outcome.Error -> logger.error(result.value)
                is Outcome.Success -> when (result.value) {
                    is UnequipResult.CharacterIsBusy -> logger.info("$figureName is busy")
                    is UnequipResult.CharacterIsInCooldown -> logger.info("$figureName is in cooldown")
                    is UnequipResult.InventoryFull -> logger.info("inventory of $figureName is full")
                    is UnequipResult.ItemNotFound -> logger.info("$figureName: item not found")
                    is UnequipResult.MissingRequiredItems -> logger.info("$figureName is missing items")
                    is UnequipResult.NotEnoughHp -> logger.info("$figureName has not enough Hp")
                    is UnequipResult.SlotNotEquipped -> logger.info("$figureName: slot is not equipped")
                    is UnequipResult.Success -> {
                        logger.info("$figureName unequipped $slot")
                    }
                }
            }
        }
    }

    companion object {
        val logger = Loggers.getLogger(CliAdapter::class.java)
    }
}
