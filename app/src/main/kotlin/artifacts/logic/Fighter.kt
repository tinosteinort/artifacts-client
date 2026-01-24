package artifacts.logic

import artifacts.business.Behaviour
import artifacts.business.DefaultActions
import artifacts.business.Figure
import artifacts.business.common.Cooldown
import artifacts.business.common.FigureData
import artifacts.business.common.Inventory
import artifacts.business.common.Position
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
            return DefaultActions.move(logger, figure, positionOfMonster)
        }

        return DefaultActions.fight(logger, figure)
    }

    private fun needsHeal(): Boolean =
        with(figureData.status) {
            hp < maxHp
        }

    private fun heal(): Cooldown =
        when (val method = detectHealingMethod()) {
            is HealingMethod.WithItem -> with(method) {
                DefaultActions.useItem(logger, figure, item, quantity)
            }

            is HealingMethod.WithoutItem -> DefaultActions.rest(logger, figure)
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

    companion object {
        val logger = Loggers.getLogger(Fighter::class.java)
    }
}
