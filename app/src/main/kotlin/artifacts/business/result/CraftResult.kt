package artifacts.business.result

import artifacts.business.common.Cooldown
import artifacts.business.common.Item
import artifacts.business.common.ItemPack

sealed class CraftResult {

    class Success(
        val items: List<ItemPack<Item.Name>>,
        val cooldown: Cooldown,
    ) : CraftResult()

    class CraftNotFound : CraftResult()
    class MissingRequiredItems : CraftResult()
    class CharacterIsBusy : CraftResult()
    class SkillLevelTooLow : CraftResult()
    class InventoryFull : CraftResult()
    class CharacterIsInCooldown : CraftResult()
    class NoWorkshopOnMap : CraftResult()
}
