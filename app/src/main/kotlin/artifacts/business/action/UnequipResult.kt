package artifacts.business.action

import artifacts.business.common.Cooldown

sealed class UnequipResult {

    class Success(
        val cooldown: Cooldown,
    ) : UnequipResult()
    class ItemNotFound : UnequipResult()
    class MissingRequiredItems : UnequipResult()
    class NotEnoughHp : UnequipResult()
    class CharacterIsBusy : UnequipResult()
    class SlotNotEquipped : UnequipResult()
    class InventoryFull : UnequipResult()
    class CharacterIsInCooldown : UnequipResult()
}
