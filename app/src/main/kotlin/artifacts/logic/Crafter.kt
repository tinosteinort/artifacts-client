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

        // copper_dagger
        val craftInfo = core.craftInfo(item)
        if (craftInfo == null) {
            logger.info("${figure.name}: item $item no craftable")
            return Cooldown.NO_COOLDOWN
        }

        // copper_bar 6x
        val missingItem: Item.Name? = getMissingItem(craftInfo)
        if (missingItem == null) {
            return craft(craftInfo)
        } else {

            val craftInfoOfMissingItem = core.craftInfo(missingItem)
            if (craftInfoOfMissingItem == null) {
                logger.info("${figure.name}: item $item no craftable")
                return Cooldown.NO_COOLDOWN
            }

            // copper_ore 10x
            val missingItem: Item.Name? = getMissingItem(craftInfoOfMissingItem)
            if (missingItem == null) {
                return craft(craftInfo)
            } else {

                val position: Position? = positionOf(missingItem)
                if (position == null) {
                    logger.info("${figure.name}: cannot find location where to get $item")
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

        return DefaultActions.craft(logger, figure, item, 1)
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
                it.item.value == neededItem.item.value
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

    private fun positionOf(missingItem: Item.Name): Position? {

        //TODO("copper_ores werden von copper_rocks gedropped")
        // map hat resource (copper_rocks)
        //  resource dropt item (copper_ore)

        val missingResourceLocation: Position? = core.maps()
            .asSequence()
            .filterNot { map -> map.content == null }
            .filter { map -> map.content!!.type == ContentType.RESOURCE }
            // der code enhällt den Code der Resource, die das Item dropt
            .filter { map -> map.content!!.code == missingItem.value }
            .map { map -> map.position }
            .firstOrNull()
        return missingResourceLocation
    }

    companion object {
        val logger = Loggers.getLogger(Crafter::class.java)
    }
}