package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class CooldownSchema(
    val total_seconds: Int,
    val remaining_seconds: Int,
    val started_at: Instant,
    val expiration: Instant,
    val reason: String
)
