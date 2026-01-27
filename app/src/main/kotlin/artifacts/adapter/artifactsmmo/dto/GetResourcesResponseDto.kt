package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetResourcesResponseDto(
    val data: Set<ResourceSchema>,
    val total: Int,
    val page: Int,
    val size: Int,
    val pages: Int,
)
