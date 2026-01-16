import artifacts.adapter.ArtifactsGameCore
import artifacts.business.Game
import artifacts.business.Items
import artifacts.business.Places
import artifacts.business.action.*
import artifacts.business.controller.Fighter
import artifacts.business.util.Loggers
import artifacts.business.util.Outcome
import java.net.http.HttpClient

class App

fun main() {
    val logger = Loggers.getLogger(App::class.java)

    val game = Game(
        ArtifactsGameCore(
            httpClient = HttpClient.newHttpClient(),
            artifactsApiUrl = "https://api.artifactsmmo.com",
            authToken = System.getenv("API_TOKEN")
        )
    )
    game.registerFigure("Henk")
    game.start()

    println("type 'exit' to quit")
    var figureName: String = "Henk"
    do {
        print("> ")
        val line = readlnOrNull()

        when (line) {
            "exit" -> {
                game.stop()
                break
            }

            "move" -> {
                game.control(figureName) { figure ->
                    when (val result = figure.move(Places.CHICKEN)) {
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

            "fight" -> {
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

            "rest" -> {
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

            "gather" -> {
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

            "craft" -> {
                game.control(figureName) { figure ->
                    when (val result = figure.craft(Items.COOKED_CHICKEN, 1)) {
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

            "equip" -> {
                val item = "aaa"
                val slot = "bbb"
                val quantity = 1
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

            "unequip" -> {
                game.control(figureName) { figure ->
                    val slot = "abc"
                    val quantity = 1
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
