package artifacts.adapter.artifactsmmo

import artifacts.adapter.artifactsmmo.dto.*
import artifacts.business.GameCore
import artifacts.business.common.*
import artifacts.business.result.*
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

    /**
     * Data of all characters is stored here. This will be updated on every action
     * of the characters. So it is possible to ask this class for up-to-date
     * information of the character, without doing a request everytime it is needed.
     *
     * The init method is needed, if the data is expected, before any action is
     * executed. Because of this, the AutoController calls it at the start.
     */
    private val characters: MutableMap<Name, CharacterSchema> = mutableMapOf()
    private val items: MutableMap<Item.Name, ItemSchema> = mutableMapOf()

    private fun updateCharacterData(data: CharacterSchema) {
        characters[Name(data.name)] = data
    }

    override fun initItems(page: Int, pageSize: Int): Outcome<InitItemsResult, GameError> {
        val request = HttpRequest
            .newBuilder(URI("$artifactsApiUrl/items?page=$page&size=$pageSize"))
            .configureHeaders()
            .GET()
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        return when (response.statusCode()) {
            200 -> {
                val itemsData = json.decodeFromString<GetAllItemsResponseDto>(response.body())
                itemsData.data.forEach { data ->
                    items[Item.Name(data.code)] = data
                }

                Outcome.success(
                    InitItemsResult.Success(
                        total = itemsData.total,
                        page = itemsData.page,
                        pageSize = pageSize,
                        pages = itemsData.pages
                    )
                )
            }

            else -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
        }
    }

    override fun init(name: Name): Outcome<InitResult, GameError> {
        val request = HttpRequest
            .newBuilder(URI("$artifactsApiUrl/my/characters"))
            .configureHeaders()
            .GET()
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        return when (response.statusCode()) {
            200 -> {
                val characterData = json.decodeFromString<GetCharactersResponseDto>(response.body())
                characterData.data.forEach { data -> updateCharacterData(data) }

                Outcome.success(
                    InitResult.Success()
                )
            }

            else -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
        }
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
                updateCharacterData(moveData.data.character)

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
                fightData.data.characters.forEach { data -> updateCharacterData(data) }

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
                updateCharacterData(gatheringData.data.character)

                Outcome.success(
                    GatherResult.Success(
                        items = gatheringData.data.details.items
                            .map {
                                ItemPack(
                                    item = Item.Name(it.code),
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
                updateCharacterData(craftingData.data.character)

                Outcome.success(
                    CraftResult.Success(
                        items = craftingData.data.details.items
                            .map {
                                ItemPack(
                                    item = Item.Name(it.code),
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
                updateCharacterData(restData.data.character)

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
                val equipData = json.decodeFromString<UnequipResponseDto>(response.body())
                updateCharacterData(equipData.data.character)

                Outcome.success(
                    EquipResult.Success(
                        cooldown = Cooldown.forSeconds(
                            equipData.data.cooldown.remaining_seconds
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
                updateCharacterData(unequipData.data.character)

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

    override fun useItem(name: Name, item: Item, quantity: Int): Outcome<UseItemResult, GameError> {
        val request = HttpRequest
            .newBuilder(URI("$artifactsApiUrl/my/$name/action/use"))
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
                val useItemData = json.decodeFromString<UseItemResponseDto>(response.body())
                updateCharacterData(useItemData.data.character)

                Outcome.success(
                    UseItemResult.Success(
                        cooldown = Cooldown.forSeconds(
                            useItemData.data.cooldown.remaining_seconds
                        ),
                    )
                )
            }

            404 -> Outcome.success(UseItemResult.ItemNotFound())
            422 -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
            476 -> Outcome.success(UseItemResult.ItemIsNotConsumable(item))
            478 -> Outcome.success(UseItemResult.MissingRequiredItems())
            486 -> Outcome.success(UseItemResult.CharacterIsBusy())
            496 -> Outcome.success(UseItemResult.ConditionsNotMet())
            498 -> Outcome.error(GameError.CharacterNotFound())
            499 -> Outcome.success(UseItemResult.CharacterIsInCooldown())
            else -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
        }
    }

    override fun giveItems(name: Name, target: Name, items: Set<ItemPack<Item.Name>>): Outcome<GiveItemsResult, GameError> {
        val request = HttpRequest
            .newBuilder(URI("$artifactsApiUrl/my/$name/action/give/item"))
            .configureHeaders()
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    json.encodeToString(
                        GiveItemsRequestDto(
                            items = items.map { itemPack ->
                                SimpleItemSchema(
                                    code = itemPack.item.value,
                                    quantity = itemPack.quantity,
                                )
                            }.toSet(),
                            character = target.value
                        )
                    )
                )
            )
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        return when (response.statusCode()) {
            200 -> {
                val giveItemsData = json.decodeFromString<GiveItemsResponseDto>(response.body())
                updateCharacterData(giveItemsData.data.character)
                updateCharacterData(giveItemsData.data.receiver_character)

                Outcome.success(
                    GiveItemsResult.Success(
                        cooldown = Cooldown.forSeconds(
                            giveItemsData.data.cooldown.remaining_seconds
                        ),
                    )
                )
            }

            404 -> Outcome.success(GiveItemsResult.ItemNotFound())
            422 -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
            478 -> Outcome.success(GiveItemsResult.MissingRequiredItems())
            486 -> Outcome.success(GiveItemsResult.CharacterIsBusy())
            497 -> Outcome.success(GiveItemsResult.InventoryFull())
            498 -> Outcome.error(GameError.CharacterNotFound())
            499 -> Outcome.success(GiveItemsResult.CharacterIsInCooldown())
            else -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
        }
    }

    private fun HttpRequest.Builder.configureHeaders(): HttpRequest.Builder {
        this.header("Authorization", "Bearer $authToken")
        this.header("Accept", "application/json")
        this.header("Content-Type", "application/json")
        return this
    }

    override fun status(name: Name): Status =
        characters[name]!!.let { data ->
            Status(
                level = data.level,
                xp = data.xp,
                maxXp = data.max_xp,
                gold = data.gold,
                hp = data.hp,
                maxHp = data.max_hp,
            )
        }

    override fun position(name: Name): Position =
        characters[name]!!.let { data ->
            Position(data.x, data.y)
        }

    override fun equipment(name: Name): Map<Slot, Equipment> =
        characters[name]!!.let { data ->
            mapOf(
                Slot.WEAPON to Equipment(Item.Name(data.weapon_slot), 1),
                Slot.SHIELD to Equipment(Item.Name(data.shield_slot), 1),
                Slot.HELMET to Equipment(Item.Name(data.helmet_slot), 1),
                Slot.BODY_ARMOR to Equipment(Item.Name(data.body_armor_slot), 1),
                Slot.LEG_ARMOR to Equipment(Item.Name(data.leg_armor_slot), 1),
                Slot.BOOTS to Equipment(Item.Name(data.boots_slot), 1),
                Slot.RING1 to Equipment(Item.Name(data.ring1_slot), 1),
                Slot.RING2 to Equipment(Item.Name(data.ring2_slot), 1),
                Slot.AMULET to Equipment(Item.Name(data.amulet_slot), 1),
                Slot.ARTIFACT1 to Equipment(Item.Name(data.artifact1_slot), 1),
                Slot.ARTIFACT2 to Equipment(Item.Name(data.artifact2_slot), 1),
                Slot.ARTIFACT3 to Equipment(Item.Name(data.artifact3_slot), 1),
                Slot.UTILITY1 to Equipment(
                    item = Item.Name(data.utility1_slot),
                    quantity = data.utility1_slot_quantity
                ),
                Slot.UTILITY2 to Equipment(
                    item = Item.Name(data.utility2_slot),
                    quantity = data.utility2_slot_quantity
                ),
                Slot.BAG to Equipment(Item.Name(data.bag_slot), 1),
            )
        }

    override fun inventory(name: Name): Inventory {
        return characters[name]!!.let { data ->
            Inventory(
                maxItems = data.inventory_max_items,
                items = data.inventory.map { slot ->
                    val item = items[Item.Name(slot.code)]!!
                    ItemPack(
                        item = Item.Details(
                            value = item.code,
                            level = item.level,
                            type = ItemType.valueOf(item.type),
                        ),
                        quantity = slot.quantity,
                    )
                }.toSet()
            )
        }
    }

    companion object {

        private val logger = Loggers.getLogger(ArtifactsGameCore::class.java)
    }
}