package artifacts.business.result

import artifacts.business.common.Cooldown

sealed class FightResult {

    class FightEnded(
        val win: Boolean,
        val opponent: String,
        val cooldown: Cooldown,
    ) : FightResult()
    class OnlyBossMobCanBeFoughtByMultipleCharacters : FightResult()
    class InventoryFull : FightResult()
    class CharacterIsInCooldown : FightResult()
    class NoMobOnMap : FightResult()
}
