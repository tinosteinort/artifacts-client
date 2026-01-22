package artifacts.logic

import artifacts.business.Behaviour
import artifacts.business.Figure
import artifacts.business.GameCore
import artifacts.business.common.Cooldown
import artifacts.business.common.Inventory
import artifacts.business.common.Item
import artifacts.business.result.RestResult
import artifacts.business.result.UseItemResult
import artifacts.business.util.Loggers

class Fighter(
    private val figure: Figure,
) : Behaviour {

    override fun control(): Cooldown {
        if (needsHeal()) {
            return heal()
        }

        return Cooldown.NO_COOLDOWN
    }

    private fun needsHeal(): Boolean =
        with(figure.status()) {
            hp < maxHp
        }

    private fun heal(): Cooldown =
        when (val method = detectHealingMethod()) {
            is HealingMethod.WithItem -> useItem(method.item, method.quantity)
            is HealingMethod.WithoutItem -> rest()
        }

    private fun detectHealingMethod(): HealingMethod {
        val inventory: Inventory = figure.inventory()

        return HealingMethod.WithoutItem()
    }

    private fun useItem(item: Item, quantity: Int): Cooldown =
        when (val result = figure.useItem(item, quantity)) {
            is UseItemResult.CharacterIsBusy -> {
                logger.info("${figure.name} is busy")
                Cooldown.NO_COOLDOWN
            }

            is UseItemResult.CharacterIsInCooldown -> {
                logger.info("${figure.name} is in cooldown")
                Cooldown.NO_COOLDOWN
            }

            is UseItemResult.ConditionsNotMet -> {
                logger.info("${figure.name} does not match conditions")
                Cooldown.NO_COOLDOWN
            }

            is UseItemResult.ItemIsNotConsumable -> {
                logger.info("${figure.name}: item '$item' is not consumable")
                Cooldown.NO_COOLDOWN
            }

            is UseItemResult.ItemNotFound -> {
                logger.info("${figure.name}: item not found")
                Cooldown.NO_COOLDOWN
            }

            is UseItemResult.MissingRequiredItems -> {
                logger.info("${figure.name} is missing items")
                Cooldown.NO_COOLDOWN
            }

            is UseItemResult.Success -> {
                logger.info("${figure.name} used item $item")
                result.cooldown
            }
        }

    private fun rest(): Cooldown =
        when (val result = figure.rest()) {
            is RestResult.CharacterIsBusy -> {
                logger.info("${figure.name} is busy")
                Cooldown.NO_COOLDOWN
            }

            is RestResult.CharacterIsInCooldown -> {
                logger.info("${figure.name} is in cooldown")
                Cooldown.NO_COOLDOWN
            }

            is RestResult.Success -> {
                logger.info("${figure.name} did a rest")
                result.cooldown
            }
        }

    companion object {
        val logger = Loggers.getLogger(Fighter::class.java)
    }
}
