package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class EquipRequestSchema(
    val cooldown: CooldownSchema,
    val slot: String,
    val item: ItemSchema,
    val character: CharacterSchema,
)
