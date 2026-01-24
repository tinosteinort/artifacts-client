package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetItemsResponseDto(
    val data: Set<ItemSchema>,
    val total: Int,
    val page: Int,
    val size: Int,
    val pages: Int,
)
