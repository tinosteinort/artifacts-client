package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class CharacterRestDataSchema(
    val cooldown: CooldownSchema,
    val hp_restored: Int,
    val character: CharacterSchema,
)
