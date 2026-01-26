package artifacts.business.common

data class CraftInfo(
    val item: Item.Name,
    val requiredLevel: Int,
    val requiredSkill: Skill,
    val neededItems: Set<ItemPack<Item.Name>>
)
