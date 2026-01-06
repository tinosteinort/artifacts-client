package artifacts.business.action

import artifacts.business.common.Cooldown

sealed class RestResult {

    class Success(
        val cooldown: Cooldown
    ) : RestResult()
    class CharacterIsBusy : RestResult()
    class CharacterIsInCooldown : RestResult()
}