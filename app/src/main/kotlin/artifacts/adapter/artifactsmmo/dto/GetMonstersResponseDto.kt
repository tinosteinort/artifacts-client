package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetMonstersResponseDto(
    val data: Set<MonsterSchema>,
    val total: Int,
    val page: Int,
    val size: Int,
    val pages: Int,
) {
}