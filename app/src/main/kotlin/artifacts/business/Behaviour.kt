package artifacts.business

import artifacts.business.common.Cooldown

interface Behaviour {

    fun control() : Cooldown
}
