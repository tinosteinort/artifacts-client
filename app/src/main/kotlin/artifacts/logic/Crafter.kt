package artifacts.logic

import artifacts.business.Behaviour
import artifacts.business.DefaultActions
import artifacts.business.Figure
import artifacts.business.GameCore
import artifacts.business.common.*
import artifacts.business.result.CraftResult
import artifacts.business.util.Loggers

class Crafter(
    private val core: GameCore,
    private val figure: Figure,
    private val item: Item.Name
) : Behaviour {

    private var errorOccurred: Boolean = false
    private var itemCrafted: Boolean = false

    override fun control(): Cooldown {
        if (errorOccurred) {
            return Cooldown.NO_COOLDOWN
        }
        if (itemCrafted) {
            return Cooldown.NO_COOLDOWN
        }

        val craftInfo = core.craftInfo(item)
        if (craftInfo == null) {
            return failed("item $item not craftable")
        }
        val missingItem: Item.Name? = getMissingItem(craftInfo)
        if (missingItem == null) {
            return craft(craftInfo)
        } else {

            val craftInfoOfMissingItem = core.craftInfo(missingItem)
            if (craftInfoOfMissingItem == null) {
                val position: Position? = positionOf(missingItem)
                if (position == null) {
                    return failed("cannot find map where to get $item")
                }

                return gatherOrFight(position)
            }

            val missingItem: Item.Name? = getMissingItem(craftInfoOfMissingItem)
            if (missingItem == null) {
                return craft(craftInfoOfMissingItem)
            } else {

                val position: Position? = positionOf(missingItem)
                if (position == null) {
                    return failed("cannot find map where to get $item")
                }

                return gatherOrFight(position)
            }
        }
    }

    private fun failed(message: String): Cooldown {
        errorOccurred = true
        logger.error("${figure.name}: task failed: $message")
        return Cooldown.NO_COOLDOWN
    }

    private fun craft(craft: CraftInfo): Cooldown {
        val workshopPosition = getWorkShopFor(craft.requiredSkill)
        if (workshopPosition != figure.data().position) {
            return DefaultActions.move(logger, figure, workshopPosition)
        }

        return when (val result = figure.craft(item, 1)) {
            is CraftResult.CharacterIsBusy -> {
                logger.info("${figure.name} is busy}")
                Cooldown.NO_COOLDOWN
            }

            is CraftResult.CharacterIsInCooldown -> {
                logger.info("${figure.name} is in cooldown")
                Cooldown.NO_COOLDOWN
            }

            is CraftResult.CraftNotFound -> {
                logger.info("${figure.name}: craft not found")
                Cooldown.NO_COOLDOWN
            }

            is CraftResult.InventoryFull -> {
                logger.info("inventory of ${figure.name} is full")
                Cooldown.NO_COOLDOWN
            }

            is CraftResult.MissingRequiredItems -> {
                logger.info("${figure.name} is missing items")
                Cooldown.NO_COOLDOWN
            }

            is CraftResult.SkillLevelTooLow -> {
                failed("crafting skill level of too low")
            }

            is CraftResult.NoWorkshopOnMap -> {
                logger.info("${figure.name}: no workshop on map")
                Cooldown.NO_COOLDOWN
            }

            is CraftResult.Success -> {
                if (craft.item == item) {
                    logger.info("${figure.name}: successfully crafted $item")
                    itemCrafted = true
                }
                result.cooldown
            }
        }
    }

    private fun gatherOrFight(position: Position): Cooldown {
        if (figure.data().position != position) {
            return DefaultActions.move(logger, figure, position)
        }

        val map = core.map(position)
        return when (map.content?.type) {
            ContentType.MONSTER -> DefaultActions.fight(logger, figure)
            ContentType.RESOURCE -> DefaultActions.gather(logger, figure)
            else -> {
                failed("nothing to fight or to gather")
            }
        }
    }

    private fun getMissingItem(craftInfo: CraftInfo): Item.Name? {
        // method: inventory.getFirstMissingItem(craftInfo)
        // or:  TODO craftInfo.getMissingItem(inventory)
        val itemToRequired: Map<Item.Name, Boolean> = craftInfo.neededItems.associate { neededItem ->
            val inventoryItemPack = figure.data().inventory.items.firstOrNull {
                it.item.name == neededItem.item.name
            }
            if (inventoryItemPack == null) {
                neededItem.item to true
            } else {
                neededItem.item to (inventoryItemPack.quantity < neededItem.quantity)
            }
        }

        return itemToRequired
            .filter { it.value }
            .map { it.key }
            .firstOrNull()
    }

    private fun getWorkShopFor(skill: Skill): Position = when (skill) {
        Skill.WEAPONCRAFTING -> Position(2, 1)
        Skill.GEARCRAFTING -> Position(3, 1)
        Skill.JEWELRYCRAFTING -> Position(1, 3)
        Skill.COOKING -> Position(1, 1)
        Skill.WOODCUTTING -> TODO("define position for $skill")
        Skill.MINING -> Position(1, 5)
        Skill.ALCHEMY -> Position(2, 3)
    }

    private fun positionOf(item: Item.Name): Position? {

        val resource: Resource? = findResourceDropping(item)
        if (resource == null) {
            logger.info("${figure.name}: no resource found that drops $item")

            // TODO find monster that drops item
            return null
        }

        val mapWithResource: Position? = core.maps()
            .asSequence()
            .filterNot { map -> map.content == null }
            .filter { map -> map.content!!.type == ContentType.RESOURCE }
            .filter { map -> map.content!!.code == resource.name }
            .map { map -> map.position }
            .firstOrNull()
        return mapWithResource
    }

    private fun findResourceDropping(item: Item.Name): Resource? =
        core.resources()
            .firstOrNull { resource -> resource.drops.contains(item) }

    companion object {
        val logger = Loggers.getLogger(Crafter::class.java)
    }
}
