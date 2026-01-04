package artifacts.adapter.dto

import java.time.LocalDateTime

data class CooldownSchema(
    val total_seconds: Int,
    val remaining_seconds: Int,
    val started_at: LocalDateTime,
    val expiration: LocalDateTime,
    val reason: String
)
