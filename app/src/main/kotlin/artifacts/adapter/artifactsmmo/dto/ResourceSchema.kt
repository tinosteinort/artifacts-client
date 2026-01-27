package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResourceSchema(
    val name: String,
    val code: String,
    val skill: String,
    val level: Int,
    val drops: Set<DropRateSchema>,
) {
}