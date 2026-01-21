package artifacts.logic

import artifacts.business.common.Item

sealed class HealingMethod {

    class WithItem(
        val item: Item,
        val quantity: Int,
    ) : HealingMethod()
    class WithoutItem : HealingMethod()
}
