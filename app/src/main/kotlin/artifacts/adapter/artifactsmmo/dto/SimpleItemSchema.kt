package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class SimpleItemSchema(
    val code: String,
    val quantity: Int,
) {
}
