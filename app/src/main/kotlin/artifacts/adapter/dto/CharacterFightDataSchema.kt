package artifacts.adapter.dto

import kotlinx.serialization.Serializable

@Serializable
data class CharacterFightDataSchema(
    val cooldown: CooldownSchema,
    val fight: CharacterFightSchema,
    val characters: List<CharacterSchema>,
)
