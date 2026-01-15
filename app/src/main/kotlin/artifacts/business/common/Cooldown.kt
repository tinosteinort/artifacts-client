package artifacts.business.common

import java.time.Instant

class Cooldown private constructor(
    private val start: Instant,
    val seconds: Int,
) {

    fun inCooldown(): Boolean =
        start.plusSeconds(seconds.toLong()) >= Instant.now()

    companion object {

        fun forSeconds(seconds: Int) =
            Cooldown(
                start = Instant.now(),
                seconds = seconds
            )

        val NO_COOLDOWN = forSeconds(0)
    }
}
