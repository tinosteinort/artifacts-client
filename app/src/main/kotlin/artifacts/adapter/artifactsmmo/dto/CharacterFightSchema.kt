package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class CharacterFightSchema(
    val result: String,
    val turns: Int,
    val opponent: String,
    val logs: List<String>
)
