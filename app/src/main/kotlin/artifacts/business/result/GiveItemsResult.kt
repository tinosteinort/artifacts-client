package artifacts.business.result

import artifacts.business.common.Cooldown

sealed class GiveItemsResult {

    class Success(
        val cooldown: Cooldown,
    ) : GiveItemsResult()

    class ItemNotFound : GiveItemsResult()
    class MissingRequiredItems : GiveItemsResult()
    class CharacterIsBusy : GiveItemsResult()
    class InventoryFull : GiveItemsResult()
    class CharacterIsInCooldown : GiveItemsResult()
}