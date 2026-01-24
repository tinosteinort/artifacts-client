package artifacts.logic

import artifacts.business.Behaviour
import artifacts.business.DefaultActions
import artifacts.business.Figure
import artifacts.business.GameCore
import artifacts.business.common.*
import artifacts.business.util.Loggers

class Fighter(
    private val core: GameCore,
    private val figure: Figure,
    private val monster: Monster,
) : Behaviour {

    override fun control(): Cooldown {
        if (needsHeal()) {
            return heal()
        }

        findMonster(monster)
            ?.let { positionOfMonster ->
                if (positionOfMonster != figure.data().position) {
                    return DefaultActions.move(logger, figure, positionOfMonster)
                }
            }
            ?: return Cooldown.NO_COOLDOWN

        return DefaultActions.fight(logger, figure)
    }

    private fun needsHeal(): Boolean =
        with(figure.data().status) {
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
        val figureData = figure.data()
        val inventory: Inventory = figureData.inventory

        val hp = figureData.status.hp
        val maxHp = figureData.status.maxHp

        val cookedChicken = inventory.items.firstOrNull { itemPack ->
            itemPack.item.value == "cooked_chicken"
        }
        if (cookedChicken != null
            && cookedChicken.quantity > 0
            && (hp / maxHp) <= 0.7
        ) {
            return HealingMethod.WithItem(cookedChicken.item, 1)
        }

        return HealingMethod.WithoutItem()
    }

    private fun findMonster(monster: Monster): Position? =
        core.maps()
            .filter { map -> map.content?.type == ContentType.MONSTER }
            .firstOrNull { map -> map.content!!.code == monster.value }
            ?.position

    companion object {
        val logger = Loggers.getLogger(Fighter::class.java)
    }
}
