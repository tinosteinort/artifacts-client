package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class GiveItemDataSchema(
    val cooldown: CooldownSchema,
    val items: Set<SimpleItemSchema>,
    val receiver_character: CharacterSchema,
    val character: CharacterSchema,
) {

}
