package artifacts.business.common

sealed class GameError() {

    class MapNotFound : GameError()
    class CharacterNotFound : GameError()
    class NoPathAvailable : GameError()
    class Generic(val message: String) : GameError()
}
