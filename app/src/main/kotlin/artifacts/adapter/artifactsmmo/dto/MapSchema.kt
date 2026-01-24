package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class MapSchema(
    val name: String,
    val skin: String,
    val x: Int,
    val y: Int,
    val layer: String,
    val access: AccessSchema,
    val interactions: InteractionSchema,
) {
}