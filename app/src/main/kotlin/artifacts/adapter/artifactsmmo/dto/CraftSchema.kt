package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class CraftSchema(
    val skill: String,
    val level: Int,
    val items: Set<SimpleItemSchema>,
    val quantity: Int,
)
