package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class GiveItemsResponseDto(
    val data: GiveItemDataSchema
) {
}
