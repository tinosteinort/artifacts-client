package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class SkillDataSchema(
    val cooldown: CooldownSchema,
    val details: SkillInfoSchema,
    val character: CharacterSchema,
)
