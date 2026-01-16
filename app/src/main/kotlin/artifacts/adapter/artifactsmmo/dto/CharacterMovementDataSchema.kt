package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class CharacterMovementDataSchema(
    val cooldown: CooldownSchema,
    val character: CharacterSchema
)
