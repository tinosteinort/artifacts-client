package artifacts.business.result

import artifacts.business.common.Cooldown

sealed class EquipResult {

    class Success(
        val cooldown: Cooldown,
    ) : EquipResult()
    class ItemNotFound : EquipResult()
    class MissingRequiredItems : EquipResult()
    class NotEnoughHp : EquipResult()
    class TooManyUtilities : EquipResult()
    class ItemIsAlreadyEquipped : EquipResult()
    class CharacterIsBusy : EquipResult()
    class SlotNotEmpty : EquipResult()
    class ConditionsNotMet : EquipResult()
    class InventoryFull : EquipResult()
    class CharacterIsInCooldown : EquipResult()
}
