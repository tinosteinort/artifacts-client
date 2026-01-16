package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class FightResponseDto(
    val data: CharacterFightDataSchema
)
