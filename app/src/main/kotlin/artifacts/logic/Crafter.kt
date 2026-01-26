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

        //val target: Item.Details = core.item(item)

        // copper_dagger
        val craftInfo = core.craftInfo(item)
        if (craftInfo == null) {
            logger.info("${figure.name}: item $item no craftable")
            return Cooldown.NO_COOLDOWN
        }

        val missingItem: Item.Name? = getMissingItem(craftInfo)
        // copper_bar 6x
        if (missingItem == null) {

            val workshopPosition = getWorkShopFor(craftInfo.requiredSkill)
            if (workshopPosition != figure.data().position) {
                return DefaultActions.move(logger, figure, workshopPosition)
            }

            return DefaultActions.craft(logger, figure, item, 1)
        } else {

            val craftInfoOfMissingItem = core.craftInfo(missingItem)
            if (craftInfoOfMissingItem == null) {
                logger.info("${figure.name}: item $item no craftable")
                return Cooldown.NO_COOLDOWN
            }

            // copper_ore 10x
            val missingItem = getMissingItem(craftInfoOfMissingItem)
            if (missingItem == null) {
                val workshopPosition = getWorkShopFor(craftInfo.requiredSkill)

                if (workshopPosition != figure.data().position) {
                    return DefaultActions.move(logger, figure, workshopPosition)
                }

                return DefaultActions.craft(logger, figure, item, 1)
            } else {

                TODO("copper_ores werden von copper_rocks gedropped")
                val missingResourceLocation: Position? = core.maps()
                    .asSequence()
                    .filterNot { map -> map.content == null }
                    .filter { map -> map.content!!.type == ContentType.RESOURCE }
                    .filter { map -> map.content!!.code == missingItem.value }
                    .map { map -> map.position }
                    .firstOrNull()

                if (missingResourceLocation == null) {
                    logger.info("${figure.name}: cannot find location where to get $item")
                    return Cooldown.NO_COOLDOWN
                }

                if (figure.data().position != missingResourceLocation) {
                    return DefaultActions.move(logger, figure, missingResourceLocation)
                }

                return DefaultActions.gather(logger, figure)

            }

        }


    }

    private fun getMissingItem(craftInfo: CraftInfo): Item.Name? {
        val itemToRequired: Map<Item.Name, Boolean> = craftInfo.neededItems.map { neededItem ->
            val inventoryItemPak = figure.data().inventory.items.firstOrNull {
                it.item.value == neededItem.item.value
            }
            if (inventoryItemPak == null) {
                neededItem.item to true
            } else {
                neededItem.item to (inventoryItemPak.quantity < neededItem.quantity)
            }
        }.toMap()

        return itemToRequired
            .filter { it.value }
            .map { it.key }
            .firstOrNull()
        //val requiredItems: Set<Item.Name> = craftInfo
        //    .neededItems
        //    .map { it.item }
        //    .toSet()
//
        //val inventoryItems = figure.data().inventory
        //    .items
        //    .map { Item.Name(it.item.value) }
        //    .toSet()
//
        //return (requiredItems - inventoryItems).firstOrNull()
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

    companion object {
        val logger = Loggers.getLogger(Crafter::class.java)
    }
}