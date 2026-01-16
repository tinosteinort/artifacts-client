package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class SkillInfoSchema(
    val xp: Int,
    val items: List<DropSchema>,
)
