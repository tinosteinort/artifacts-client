package artifacts.business.common

data class Inventory(
    val maxItems: Int,
    val items: Set<ItemPack<Item.Details>>
)
