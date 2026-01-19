package artifacts.business

import artifacts.business.common.Cooldown

interface Behaviour {

    fun init()
    fun control() : Cooldown
}
