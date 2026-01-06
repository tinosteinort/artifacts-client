package artifacts.adapter.dto

import kotlinx.serialization.Serializable

@Serializable
data class RestResponseDto(
    val data: CharacterRestDataSchema
)
