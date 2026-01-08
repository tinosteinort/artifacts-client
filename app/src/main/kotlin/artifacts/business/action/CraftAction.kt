package artifacts.business.action

import artifacts.business.Action
import artifacts.business.Game
import artifacts.business.common.Cooldown
import artifacts.business.util.Loggers
import artifacts.business.util.Outcome

class CraftAction(
    val item: String,
    val quantity: Int,
) : Action {

    override fun execute(game: Game, figureName: String): Cooldown {
        when (val result = game.craft(figureName, item, quantity)) {
            is Outcome.Error -> logger.error("${result.value::class.java}")
            is Outcome.Success -> when (result.value) {
                is CraftResult.CharacterIsBusy -> logger.info("$figureName is busy")
                is CraftResult.CharacterIsInCooldown -> logger.info("$figureName is in cooldown")
                is CraftResult.CraftNotFound -> logger.info("$figureName: craft not found")
                is CraftResult.InventoryFull -> logger.info("inventory of $figureName is full")
                is CraftResult.MissingRequiredItems -> logger.info("$figureName is missing items")
                is CraftResult.SkillLevelTooLow -> logger.info("crafting skill level of $figureName too low")
                is CraftResult.Success -> {
                    logger.info("$figureName crafted ${result.value.items.size} items")
                    return result.value.cooldown
                }
            }
        }

        return Cooldown.forSeconds(5)
    }

    companion object {
        val logger = Loggers.getLogger(CraftAction::class.java)
    }
}
