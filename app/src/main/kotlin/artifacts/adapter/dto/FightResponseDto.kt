package artifacts.adapter.dto

import kotlinx.serialization.Serializable

@Serializable
class FightResponseDto(
    val data: CharacterFightDataSchema
)
