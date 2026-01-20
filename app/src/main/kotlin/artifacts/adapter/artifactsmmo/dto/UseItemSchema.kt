package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
class UseItemSchema(
    val cooldown: CooldownSchema,
    val item: ItemSchema,
    val character: CharacterSchema,
) {
}