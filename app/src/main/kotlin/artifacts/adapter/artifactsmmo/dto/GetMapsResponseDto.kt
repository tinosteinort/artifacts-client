package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetMapsResponseDto(
    val data: Set<MapSchema>,
    val total: Int,
    val page: Int,
    val size: Int,
    val pages: Int,
) {
}