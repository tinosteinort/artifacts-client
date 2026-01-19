package artifacts.business.result

import artifacts.business.common.Cooldown
import artifacts.business.common.ItemDrop

sealed class CraftResult {

    class Success(
        val items: List<ItemDrop>,
        val cooldown: Cooldown,
    ) : CraftResult()

    class CraftNotFound : CraftResult()
    class MissingRequiredItems : CraftResult()
    class CharacterIsBusy : CraftResult()
    class SkillLevelTooLow : CraftResult()
    class InventoryFull : CraftResult()
    class CharacterIsInCooldown : CraftResult()
}
