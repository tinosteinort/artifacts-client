package artifacts.adapter.artifactsmmo

import artifacts.adapter.artifactsmmo.dto.*
import artifacts.business.GameCore
import artifacts.business.Item
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

    override fun move(character: String, position: Position): Outcome<MoveResult, GameError> {
        val request = HttpRequest
            .newBuilder(URI("$artifactsApiUrl/my/${character}/action/move"))
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
                Outcome.Companion.success(
                    MoveResult.Success(
                        cooldown = Cooldown.Companion.forSeconds(
                            moveData.data.cooldown.remaining_seconds
                        )
                    )
                )
            }

            404 -> Outcome.Companion.error(GameError.MapNotFound())
            422 -> Outcome.Companion.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
            486 -> Outcome.Companion.success(MoveResult.CharacterIsBusy())
            490 -> Outcome.Companion.success(MoveResult.AlreadyThere())
            496 -> Outcome.Companion.success(MoveResult.ConditionsNotMet())
            498 -> Outcome.Companion.error(GameError.CharacterNotFound())
            499 -> Outcome.Companion.success(MoveResult.CharacterIsInCooldown())
            595 -> Outcome.Companion.error(GameError.NoPathAvailable())
            596 -> Outcome.Companion.success(MoveResult.MapIsBlocked())
            else -> Outcome.Companion.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
        }
    }

    override fun fight(character: String): Outcome<FightResult, GameError> {
        val request = HttpRequest
            .newBuilder(URI("$artifactsApiUrl/my/${character}/action/fight"))
            .configureHeaders()
            .POST(HttpRequest.BodyPublishers.noBody())
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        return when (response.statusCode()) {
            200 -> {
                val fightData = json.decodeFromString<FightResponseDto>(response.body())

                Outcome.Companion.success(
                    FightResult.FightEnded(
                        win = fightData.data.fight.result == "win",
                        opponent = fightData.data.fight.opponent,
                        cooldown = Cooldown.Companion.forSeconds(
                            fightData.data.cooldown.remaining_seconds
                        ),
                    )
                )
            }

            422 -> Outcome.Companion.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
            486 -> Outcome.Companion.success(FightResult.OnlyBossMonsterCanBeFoughtByMultipleCharacters())
            497 -> Outcome.Companion.success(FightResult.InventoryFull())
            498 -> Outcome.Companion.error(GameError.CharacterNotFound())
            499 -> Outcome.Companion.success(FightResult.CharacterIsInCooldown())
            598 -> Outcome.Companion.success(FightResult.NoMonsterOnMap())
            else -> Outcome.Companion.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
        }
    }

    override fun gather(character: String): Outcome<GatherResult, GameError> {
        val request = HttpRequest
            .newBuilder(URI("$artifactsApiUrl/my/${character}/action/gathering"))
            .configureHeaders()
            .POST(HttpRequest.BodyPublishers.noBody())
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        return when (response.statusCode()) {
            200 -> {
                val gatheringData = json.decodeFromString<GatherResponseDto>(response.body())

                Outcome.Companion.success(
                    GatherResult.Success(
                        items = gatheringData.data.details.items
                            .map {
                                ItemDrop(
                                    item = it.code,
                                    quantity = it.quantity
                                )
                            },
                        cooldown = Cooldown.Companion.forSeconds(
                            gatheringData.data.cooldown.remaining_seconds
                        ),
                    )
                )
            }

            422 -> Outcome.Companion.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
            486 -> Outcome.Companion.success(GatherResult.CharacterIsBusy())
            493 -> Outcome.Companion.success(GatherResult.SkillLevelTooLow())
            497 -> Outcome.Companion.success(GatherResult.InventoryFull())
            498 -> Outcome.Companion.error(GameError.CharacterNotFound())
            499 -> Outcome.Companion.success(GatherResult.CharacterIsInCooldown())
            598 -> Outcome.Companion.success(GatherResult.NoResourceOnMap())
            else -> Outcome.Companion.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
        }
    }

    override fun craft(character: String, item: Item, quantity: Int): Outcome<CraftResult, GameError> {
        val request = HttpRequest
            .newBuilder(URI("$artifactsApiUrl/my/${character}/action/crafting"))
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

                Outcome.Companion.success(
                    CraftResult.Success(
                        items = craftingData.data.details.items
                            .map {
                                ItemDrop(
                                    item = it.code,
                                    quantity = it.quantity
                                )
                            },
                        cooldown = Cooldown.Companion.forSeconds(
                            craftingData.data.cooldown.remaining_seconds
                        ),
                    )
                )
            }

            404 -> Outcome.Companion.success(CraftResult.CraftNotFound())
            422 -> Outcome.Companion.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
            478 -> Outcome.Companion.success(CraftResult.MissingRequiredItems())
            486 -> Outcome.Companion.success(CraftResult.CharacterIsBusy())
            493 -> Outcome.Companion.success(CraftResult.SkillLevelTooLow())
            497 -> Outcome.Companion.success(CraftResult.InventoryFull())
            498 -> Outcome.Companion.error(GameError.CharacterNotFound())
            499 -> Outcome.Companion.success(CraftResult.CharacterIsInCooldown())
            else -> Outcome.Companion.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
        }
    }

    override fun rest(character: String): Outcome<RestResult, GameError> {
        val request = HttpRequest
            .newBuilder(URI("$artifactsApiUrl/my/${character}/action/rest"))
            .configureHeaders()
            .POST(HttpRequest.BodyPublishers.noBody())
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        return when (response.statusCode()) {
            200 -> {
                val restData = json.decodeFromString<RestResponseDto>(response.body())

                Outcome.Companion.success(
                    RestResult.Success(
                        cooldown = Cooldown.Companion.forSeconds(
                            restData.data.cooldown.remaining_seconds
                        ),
                    )
                )
            }

            422 -> Outcome.Companion.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
            486 -> Outcome.Companion.success(RestResult.CharacterIsBusy())
            498 -> Outcome.Companion.error(GameError.CharacterNotFound())
            499 -> Outcome.Companion.success(RestResult.CharacterIsInCooldown())
            else -> Outcome.Companion.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
        }
    }

    override fun equip(character: String, item: Item, slot: String, quantity: Int): Outcome<EquipResult, GameError> {
        val request = HttpRequest
            .newBuilder(URI("$artifactsApiUrl/my/${character}/action/equip"))
            .configureHeaders()
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    """
                    {
                      "code": "${item.value}",
                      "slot": "$slot",
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

                Outcome.Companion.success(
                    EquipResult.Success(
                        cooldown = Cooldown.Companion.forSeconds(
                            unequipData.data.cooldown.remaining_seconds
                        ),
                    )
                )
            }

            404 -> Outcome.Companion.success(EquipResult.ItemNotFound())
            422 -> Outcome.Companion.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
            478 -> Outcome.Companion.success(EquipResult.MissingRequiredItems())
            483 -> Outcome.Companion.success(EquipResult.NotEnoughHp())
            484 -> Outcome.Companion.success(EquipResult.TooManyUtilities())
            485 -> Outcome.Companion.success(EquipResult.ItemIsAlreadyEquipped())
            486 -> Outcome.Companion.success(EquipResult.CharacterIsBusy())
            491 -> Outcome.Companion.success(EquipResult.SlotNotEmpty())
            496 -> Outcome.Companion.success(EquipResult.ConditionsNotMet())
            497 -> Outcome.Companion.success(EquipResult.InventoryFull())
            498 -> Outcome.Companion.error(GameError.CharacterNotFound())
            499 -> Outcome.Companion.success(EquipResult.CharacterIsInCooldown())
            else -> Outcome.Companion.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
        }
    }

    override fun unequip(character: String, slot: String, quantity: Int): Outcome<UnequipResult, GameError> {
        val request = HttpRequest
            .newBuilder(URI("$artifactsApiUrl/my/${character}/action/unequip"))
            .configureHeaders()
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    """
                    {
                      "slot": "$slot",
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

                Outcome.Companion.success(
                    UnequipResult.Success(
                        cooldown = Cooldown.Companion.forSeconds(
                            unequipData.data.cooldown.remaining_seconds
                        ),
                    )
                )
            }

            404 -> Outcome.Companion.success(UnequipResult.ItemNotFound())
            422 -> Outcome.Companion.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
            478 -> Outcome.Companion.success(UnequipResult.MissingRequiredItems())
            483 -> Outcome.Companion.success(UnequipResult.NotEnoughHp())
            486 -> Outcome.Companion.success(UnequipResult.CharacterIsBusy())
            491 -> Outcome.Companion.success(UnequipResult.SlotNotEquipped())
            497 -> Outcome.Companion.success(UnequipResult.InventoryFull())
            498 -> Outcome.Companion.error(GameError.CharacterNotFound())
            499 -> Outcome.Companion.success(UnequipResult.CharacterIsInCooldown())
            else -> Outcome.Companion.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
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