package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class GiveItemsRequestDto(
    val items: Set<SimpleItemSchema>,
    val character: String,
) {
}