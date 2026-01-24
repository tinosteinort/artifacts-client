package artifacts.logic

import artifacts.business.Behaviour
import artifacts.business.Figure
import artifacts.business.common.*
import artifacts.business.result.FightResult
import artifacts.business.result.MoveResult
import artifacts.business.result.RestResult
import artifacts.business.result.UseItemResult
import artifacts.business.util.Loggers

class Fighter(
    private val figure: Figure,
    private val positionOfMonster: Position,
) : Behaviour {

    private lateinit var figureData: FigureData
    override fun control(): Cooldown {
        figureData = figure.data()
        if (needsHeal()) {
            return heal()
        }

        if (positionOfMonster != figureData.position) {
            return move(positionOfMonster)
        }

        return fight()
    }

    private fun needsHeal(): Boolean =
        with(figureData.status) {
            hp < maxHp
        }

    private fun heal(): Cooldown =
        when (val method = detectHealingMethod()) {
            is HealingMethod.WithItem -> with(method) {
                useItem(item, quantity)
            }

            is HealingMethod.WithoutItem -> rest()
        }

    private fun detectHealingMethod(): HealingMethod {
        val inventory: Inventory = figureData.inventory

        val hp = figureData.status.hp
        val maxHp = figureData.status.maxHp

        val cookedChicken = inventory.items.filter { itemPack ->
            itemPack.item.value == "cooked_chicken"
        }.firstOrNull()
        if (cookedChicken != null
            && cookedChicken.quantity > 0
            && (hp / maxHp) <= 0.7
        ) {
            return HealingMethod.WithItem(cookedChicken.item, 1)
        }

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

    private fun move(position: Position): Cooldown =
        when (val result = figure.move(position)) {
            is MoveResult.AlreadyThere -> {
                logger.info("${figure.name} is already there")
                Cooldown.NO_COOLDOWN
            }

            is MoveResult.CharacterIsBusy -> {
                logger.info("${figure.name} is busy")
                Cooldown.NO_COOLDOWN
            }

            is MoveResult.CharacterIsInCooldown -> {
                logger.info("${figure.name} is in cooldown")
                Cooldown.NO_COOLDOWN
            }

            is MoveResult.ConditionsNotMet -> {
                logger.info("${figure.name} does not match conditions")
                Cooldown.NO_COOLDOWN
            }

            is MoveResult.MapIsBlocked -> {
                logger.info("map is blocked")
                Cooldown.NO_COOLDOWN
            }

            is MoveResult.Success -> {
                logger.info("${figure.name} move done")
                result.cooldown
            }
        }

    private fun fight(): Cooldown =
        when (val result = figure.fight()) {
            is FightResult.FightEnded -> {
                if (result.win) {
                    logger.info("${figure.name} won the fight against ${result.opponent}")
                } else {
                    logger.info("${figure.name} lost the fight against ${result.opponent}")
                }
                result.cooldown
            }

            is FightResult.CharacterIsInCooldown -> {
                logger.info("${figure.name} is in cooldown")
                Cooldown.NO_COOLDOWN
            }

            is FightResult.InventoryFull -> {
                logger.info("inventory of ${figure.name} is full")
                Cooldown.NO_COOLDOWN
            }

            is FightResult.NoMonsterOnMap -> {
                logger.info("${figure.name} cannot fight, no monster on map")
                Cooldown.NO_COOLDOWN
            }

            is FightResult.OnlyBossMonsterCanBeFoughtByMultipleCharacters -> {
                logger.info("${figure.name}: only boss monster can be fought by multiple characters")
                Cooldown.NO_COOLDOWN
            }
        }

    companion object {
        val logger = Loggers.getLogger(Fighter::class.java)
    }
}
