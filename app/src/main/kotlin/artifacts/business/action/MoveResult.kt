package artifacts.business.action

sealed class MoveResult {

    class Success : MoveResult()
    class AlreadyThere : MoveResult()
    class CharacterIsInCooldown : MoveResult()
    class CharacterIsBusy : MoveResult()
    class ConditionsNotMet : MoveResult()
    class MapIsBlocked : MoveResult()
}
