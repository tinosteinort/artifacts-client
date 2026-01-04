package artifacts.business.action

sealed class FightResult {

    class FightEnded : FightResult()
    class OnlyBossMonsterCanBeFoughtByMultipleCharacters : FightResult()
    class InventoryFull : FightResult()
    class CharacterIsInCooldown : FightResult()
    class NoMonsterOnMap : FightResult()
}
