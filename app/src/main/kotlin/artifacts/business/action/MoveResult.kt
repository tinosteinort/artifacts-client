package artifacts.business.action

import artifacts.business.common.Cooldown

sealed class MoveResult {

    class Success(
        val cooldown: Cooldown
    ) : MoveResult()
    class AlreadyThere : MoveResult()
    class CharacterIsInCooldown : MoveResult()
    class CharacterIsBusy : MoveResult()
    class ConditionsNotMet : MoveResult()
    class MapIsBlocked : MoveResult()
}
