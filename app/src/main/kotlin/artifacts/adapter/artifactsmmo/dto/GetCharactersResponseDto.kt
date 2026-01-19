package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
class GetCharactersResponseDto(
    val data: List<CharacterSchema>
)
