package artifacts.business.util

import artifacts.business.common.GameError

class GameException(val error: GameError) : RuntimeException()
