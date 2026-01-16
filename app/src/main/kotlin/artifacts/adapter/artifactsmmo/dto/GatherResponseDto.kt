package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class GatherResponseDto(
    val data: SkillDataSchema,
)
