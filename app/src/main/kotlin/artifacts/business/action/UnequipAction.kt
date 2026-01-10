package artifacts.business.action

import artifacts.business.Action
import artifacts.business.GameCore
import artifacts.business.common.Cooldown
import artifacts.business.util.Loggers
import artifacts.business.util.Outcome

class UnequipAction(
    val slot: String,
    val quantity: Int,
) : Action {

    override fun execute(core: GameCore, figureName: String): Cooldown {
        when (val result = core.unequip(figureName, slot, quantity)) {
            is Outcome.Error -> logger.error("${result.value::class.java}")
            is Outcome.Success -> when(result.value) {
                is UnequipResult.CharacterIsBusy -> logger.info("$figureName is busy")
                is UnequipResult.CharacterIsInCooldown -> logger.info("$figureName is in cooldown")
                is UnequipResult.InventoryFull -> logger.info("inventory of $figureName is full")
                is UnequipResult.ItemNotFound -> logger.info("$figureName: item not found")
                is UnequipResult.MissingRequiredItems -> logger.info("$figureName is missing items")
                is UnequipResult.NotEnoughHp -> logger.info("$figureName has not enough Hp")
                is UnequipResult.SlotNotEquipped -> logger.info("$figureName: slot is not equipped")
                is UnequipResult.Success -> {
                    logger.info("$figureName unequipped $slot")
                    return result.value.cooldown
                }
            }
        }

        return Cooldown.forSeconds(5)
    }

    companion object {
        val logger = Loggers.getLogger(UnequipAction::class.java)
    }
}
