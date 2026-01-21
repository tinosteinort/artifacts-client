package artifacts.business.result

import artifacts.business.common.Cooldown
import artifacts.business.common.Item
import artifacts.business.common.ItemPack

sealed class GatherResult {

    class Success(
        val items: List<ItemPack<Item.Name>>,
        val cooldown: Cooldown,
    ) : GatherResult()

    class CharacterIsBusy : GatherResult()
    class SkillLevelTooLow : GatherResult()
    class InventoryFull : GatherResult()
    class CharacterIsInCooldown : GatherResult()
    class NoResourceOnMap : GatherResult()
}
