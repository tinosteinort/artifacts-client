package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class RestResponseDto(
    val data: CharacterRestDataSchema
)
