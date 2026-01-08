package artifacts.business.action

import artifacts.business.common.Cooldown
import artifacts.business.common.ItemDrop

sealed class GatherResult {

    class Success(
        val items: List<ItemDrop>,
        val cooldown: Cooldown,
    ) : GatherResult()
    class CharacterIsBusy : GatherResult()
    class SkillLevelTooLow : GatherResult()
    class InventoryFull : GatherResult()
    class CharacterIsInCooldown : GatherResult()
    class NoResourceOnMap : GatherResult()
}
