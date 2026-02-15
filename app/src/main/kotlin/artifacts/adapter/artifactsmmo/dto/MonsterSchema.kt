package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class MonsterSchema(
    val code: String,
    val name: String,
    val level: Int,
    val type: String,
    val hp: Int,
    val drops: Set<DropRateSchema>,
)
