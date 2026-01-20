package artifacts.business.result

import artifacts.business.common.Cooldown
import artifacts.business.common.Item

sealed class UseItemResult {

    class Success(
        val cooldown: Cooldown,
    ) : UseItemResult()
    class ItemNotFound : UseItemResult()
    class ItemIsNotConsumable(
        val item: Item
    ) : UseItemResult()
    class MissingRequiredItems : UseItemResult()
    class CharacterIsBusy : UseItemResult()
    class ConditionsNotMet : UseItemResult()
    class CharacterIsInCooldown : UseItemResult()
}
