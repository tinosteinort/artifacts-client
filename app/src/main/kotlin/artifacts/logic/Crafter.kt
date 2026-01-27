package artifacts.logic

import artifacts.business.Behaviour
import artifacts.business.DefaultActions
import artifacts.business.Figure
import artifacts.business.GameCore
import artifacts.business.common.*
import artifacts.business.util.Loggers

class Crafter(
    private val core: GameCore,
    private val figure: Figure,
    private val item: Item.Name
) : Behaviour {

    override fun control(): Cooldown {

        val craftInfo = core.craftInfo(item)
        if (craftInfo == null) {
            logger.info("${figure.name}: item $item not craftable")
            return Cooldown.NO_COOLDOWN
        }

        val missingItem: Item.Name? = getMissingItem(craftInfo)
        if (missingItem == null) {
            return craft(craftInfo)
        } else {

            val craftInfoOfMissingItem = core.craftInfo(missingItem)
            if (craftInfoOfMissingItem == null) {
                logger.info("${figure.name}: item $item not craftable")
                return Cooldown.NO_COOLDOWN
            }

            val missingItem: Item.Name? = getMissingItem(craftInfoOfMissingItem)
            if (missingItem == null) {
                return craft(craftInfoOfMissingItem)
            } else {

                val position: Position? = positionOf(missingItem)
                if (position == null) {
                    logger.info("${figure.name}: cannot find map where to get $item")
                    return Cooldown.NO_COOLDOWN
                }

                return gather(position)
            }
        }
    }

    private fun craft(craft: CraftInfo) : Cooldown {
        val workshopPosition = getWorkShopFor(craft.requiredSkill)
        if (workshopPosition != figure.data().position) {
            return DefaultActions.move(logger, figure, workshopPosition)
        }

        return DefaultActions.craft(logger, figure, craft.item, 1)
    }

    private fun gather(position: Position): Cooldown {
        if (figure.data().position != position) {
            return DefaultActions.move(logger, figure, position)
        }

        return DefaultActions.gather(logger, figure)
    }

    private fun getMissingItem(craftInfo: CraftInfo): Item.Name? {
        val itemToRequired: Map<Item.Name, Boolean> = craftInfo.neededItems.associate { neededItem ->
            val inventoryItemPak = figure.data().inventory.items.firstOrNull {
                it.item.name == neededItem.item.name
            }
            if (inventoryItemPak == null) {
                neededItem.item to true
            } else {
                neededItem.item to (inventoryItemPak.quantity < neededItem.quantity)
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
        Skill.JEWELRYCRAFTING -> TODO("define position for $skill")
        Skill.COOKING -> Position(1, 1)
        Skill.WOODCUTTING -> TODO("define position for $skill")
        Skill.MINING -> Position(1, 5)
        Skill.ALCHEMY -> TODO("define position for $skill")
    }

    private fun positionOf(item: Item.Name): Position? {

        val resource: Resource? = findResourceDropping(item)

        if (resource == null) {
            logger.info("${figure.name}: no resource found that drops $item")
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
