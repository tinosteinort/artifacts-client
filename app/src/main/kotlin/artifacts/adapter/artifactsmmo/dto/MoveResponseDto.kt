package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class MoveResponseDto(
    val data: CharacterMovementDataSchema
)
