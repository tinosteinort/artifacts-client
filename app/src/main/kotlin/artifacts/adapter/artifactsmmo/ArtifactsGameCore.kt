package artifacts.adapter.artifactsmmo

import artifacts.adapter.artifactsmmo.dto.*
import artifacts.business.GameCore
import artifacts.business.Item
import artifacts.business.Name
import artifacts.business.Slot
import artifacts.business.action.*
import artifacts.business.common.Cooldown
import artifacts.business.common.GameError
import artifacts.business.common.ItemDrop
import artifacts.business.common.Position
import artifacts.business.util.Loggers
import artifacts.business.util.Outcome
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class ArtifactsGameCore(
    private val httpClient: HttpClient,
    private val artifactsApiUrl: String,
    private val authToken: String
) : GameCore {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    override fun move(name: Name, position: Position): Outcome<MoveResult, GameError> {
        val request = HttpRequest
            .newBuilder(URI("$artifactsApiUrl/my/$name/action/move"))
            .configureHeaders()
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    """
                    {
                      "x": ${position.x},
                      "y": ${position.y}
                    }    
                    """.trimIndent()
                )
            )
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        return when (response.statusCode()) {
            200 -> {
                val moveData = json.decodeFromString<MoveResponseDto>(response.body())
                Outcome.success(
                    MoveResult.Success(
                        cooldown = Cooldown.forSeconds(
                            moveData.data.cooldown.remaining_seconds
                        )
                    )
                )
            }

            404 -> Outcome.error(GameError.MapNotFound())
            422 -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
            486 -> Outcome.success(MoveResult.CharacterIsBusy())
            490 -> Outcome.success(MoveResult.AlreadyThere())
            496 -> Outcome.success(MoveResult.ConditionsNotMet())
            498 -> Outcome.error(GameError.CharacterNotFound())
            499 -> Outcome.success(MoveResult.CharacterIsInCooldown())
            595 -> Outcome.error(GameError.NoPathAvailable())
            596 -> Outcome.success(MoveResult.MapIsBlocked())
            else -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
        }
    }

    override fun fight(name: Name): Outcome<FightResult, GameError> {
        val request = HttpRequest
            .newBuilder(URI("$artifactsApiUrl/my/$name/action/fight"))
            .configureHeaders()
            .POST(HttpRequest.BodyPublishers.noBody())
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        return when (response.statusCode()) {
            200 -> {
                val fightData = json.decodeFromString<FightResponseDto>(response.body())

                Outcome.success(
                    FightResult.FightEnded(
                        win = fightData.data.fight.result == "win",
                        opponent = fightData.data.fight.opponent,
                        cooldown = Cooldown.forSeconds(
                            fightData.data.cooldown.remaining_seconds
                        ),
                    )
                )
            }

            422 -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
            486 -> Outcome.success(FightResult.OnlyBossMonsterCanBeFoughtByMultipleCharacters())
            497 -> Outcome.success(FightResult.InventoryFull())
            498 -> Outcome.error(GameError.CharacterNotFound())
            499 -> Outcome.success(FightResult.CharacterIsInCooldown())
            598 -> Outcome.success(FightResult.NoMonsterOnMap())
            else -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
        }
    }

    override fun gather(name: Name): Outcome<GatherResult, GameError> {
        val request = HttpRequest
            .newBuilder(URI("$artifactsApiUrl/my/$name/action/gathering"))
            .configureHeaders()
            .POST(HttpRequest.BodyPublishers.noBody())
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        return when (response.statusCode()) {
            200 -> {
                val gatheringData = json.decodeFromString<GatherResponseDto>(response.body())

                Outcome.success(
                    GatherResult.Success(
                        items = gatheringData.data.details.items
                            .map {
                                ItemDrop(
                                    item = it.code,
                                    quantity = it.quantity
                                )
                            },
                        cooldown = Cooldown.forSeconds(
                            gatheringData.data.cooldown.remaining_seconds
                        ),
                    )
                )
            }

            422 -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
            486 -> Outcome.success(GatherResult.CharacterIsBusy())
            493 -> Outcome.success(GatherResult.SkillLevelTooLow())
            497 -> Outcome.success(GatherResult.InventoryFull())
            498 -> Outcome.error(GameError.CharacterNotFound())
            499 -> Outcome.success(GatherResult.CharacterIsInCooldown())
            598 -> Outcome.success(GatherResult.NoResourceOnMap())
            else -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
        }
    }

    override fun craft(name: Name, item: Item, quantity: Int): Outcome<CraftResult, GameError> {
        val request = HttpRequest
            .newBuilder(URI("$artifactsApiUrl/my/$name/action/crafting"))
            .configureHeaders()
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    """
                    {
                      "code": "${item.value}",
                      "quantity": $quantity
                    }    
                    """.trimIndent()
                )
            )
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        return when (response.statusCode()) {
            200 -> {
                val craftingData = json.decodeFromString<CraftingResponseDto>(response.body())

                Outcome.success(
                    CraftResult.Success(
                        items = craftingData.data.details.items
                            .map {
                                ItemDrop(
                                    item = it.code,
                                    quantity = it.quantity
                                )
                            },
                        cooldown = Cooldown.forSeconds(
                            craftingData.data.cooldown.remaining_seconds
                        ),
                    )
                )
            }

            404 -> Outcome.success(CraftResult.CraftNotFound())
            422 -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
            478 -> Outcome.success(CraftResult.MissingRequiredItems())
            486 -> Outcome.success(CraftResult.CharacterIsBusy())
            493 -> Outcome.success(CraftResult.SkillLevelTooLow())
            497 -> Outcome.success(CraftResult.InventoryFull())
            498 -> Outcome.error(GameError.CharacterNotFound())
            499 -> Outcome.success(CraftResult.CharacterIsInCooldown())
            else -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
        }
    }

    override fun rest(name: Name): Outcome<RestResult, GameError> {
        val request = HttpRequest
            .newBuilder(URI("$artifactsApiUrl/my/$name/action/rest"))
            .configureHeaders()
            .POST(HttpRequest.BodyPublishers.noBody())
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        return when (response.statusCode()) {
            200 -> {
                val restData = json.decodeFromString<RestResponseDto>(response.body())

                Outcome.success(
                    RestResult.Success(
                        cooldown = Cooldown.forSeconds(
                            restData.data.cooldown.remaining_seconds
                        ),
                    )
                )
            }

            422 -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
            486 -> Outcome.success(RestResult.CharacterIsBusy())
            498 -> Outcome.error(GameError.CharacterNotFound())
            499 -> Outcome.success(RestResult.CharacterIsInCooldown())
            else -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
        }
    }

    override fun equip(name: Name, item: Item, slot: Slot, quantity: Int): Outcome<EquipResult, GameError> {
        val request = HttpRequest
            .newBuilder(URI("$artifactsApiUrl/my/$name/action/equip"))
            .configureHeaders()
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    """
                    {
                      "code": "${item.value}",
                      "slot": "${slot.value}",
                      "quantity": $quantity
                    }    
                    """.trimIndent()
                )
            )
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        return when (response.statusCode()) {
            200 -> {
                val unequipData = json.decodeFromString<UnequipResponseDto>(response.body())

                Outcome.success(
                    EquipResult.Success(
                        cooldown = Cooldown.forSeconds(
                            unequipData.data.cooldown.remaining_seconds
                        ),
                    )
                )
            }

            404 -> Outcome.success(EquipResult.ItemNotFound())
            422 -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
            478 -> Outcome.success(EquipResult.MissingRequiredItems())
            483 -> Outcome.success(EquipResult.NotEnoughHp())
            484 -> Outcome.success(EquipResult.TooManyUtilities())
            485 -> Outcome.success(EquipResult.ItemIsAlreadyEquipped())
            486 -> Outcome.success(EquipResult.CharacterIsBusy())
            491 -> Outcome.success(EquipResult.SlotNotEmpty())
            496 -> Outcome.success(EquipResult.ConditionsNotMet())
            497 -> Outcome.success(EquipResult.InventoryFull())
            498 -> Outcome.error(GameError.CharacterNotFound())
            499 -> Outcome.success(EquipResult.CharacterIsInCooldown())
            else -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
        }
    }

    override fun unequip(name: Name, slot: Slot, quantity: Int): Outcome<UnequipResult, GameError> {
        val request = HttpRequest
            .newBuilder(URI("$artifactsApiUrl/my/$name/action/unequip"))
            .configureHeaders()
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    """
                    {
                      "slot": "${slot.value}",
                      "quantity": $quantity
                    }    
                    """.trimIndent()
                )
            )
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        return when (response.statusCode()) {
            200 -> {
                val unequipData = json.decodeFromString<UnequipResponseDto>(response.body())

                Outcome.success(
                    UnequipResult.Success(
                        cooldown = Cooldown.forSeconds(
                            unequipData.data.cooldown.remaining_seconds
                        ),
                    )
                )
            }

            404 -> Outcome.success(UnequipResult.ItemNotFound())
            422 -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
            478 -> Outcome.success(UnequipResult.MissingRequiredItems())
            483 -> Outcome.success(UnequipResult.NotEnoughHp())
            486 -> Outcome.success(UnequipResult.CharacterIsBusy())
            491 -> Outcome.success(UnequipResult.SlotNotEquipped())
            497 -> Outcome.success(UnequipResult.InventoryFull())
            498 -> Outcome.error(GameError.CharacterNotFound())
            499 -> Outcome.success(UnequipResult.CharacterIsInCooldown())
            else -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
        }
    }

    private fun HttpRequest.Builder.configureHeaders(): HttpRequest.Builder {
        this.header("Authorization", "Bearer $authToken")
        this.header("Accept", "application/json")
        this.header("Content-Type", "application/json")
        return this
    }

    companion object {

        private val logger = Loggers.getLogger(ArtifactsGameCore::class.java)
    }
}