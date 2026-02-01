package artifacts.logic

import artifacts.business.common.FigureData
import artifacts.business.common.Item

sealed class HealingMethod {

    class WithItem(
        val item: Item,
        val quantity: Int,
    ) : HealingMethod()

    class WithoutItem : HealingMethod()

    companion object {

        fun forFigure(data: FigureData): HealingMethod {
            val hp = data.status.hp
            val maxHp = data.status.maxHp

            val cookedChicken = data.inventory.items.firstOrNull { itemPack ->
                itemPack.item.name == "cooked_chicken"
            }
            if (cookedChicken != null
                && cookedChicken.quantity > 0
                && (hp / maxHp) <= 0.7
            ) {
                return WithItem(cookedChicken.item, 1)
            }

            return WithoutItem()
        }
    }
}
