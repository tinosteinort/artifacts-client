package artifacts.business.common

data class CraftInfo(
    val requiredLevel: Int,
    val neededItems: Set<ItemPack<Item.Name>>
)
