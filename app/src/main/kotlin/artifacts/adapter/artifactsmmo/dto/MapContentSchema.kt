package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class MapContentSchema(
    val type: String,
    val code: String,
)
