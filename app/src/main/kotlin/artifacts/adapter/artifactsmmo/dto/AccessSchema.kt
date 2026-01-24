package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class AccessSchema(
    val type: String,
    val conditions: Set<ConditionSchema>,
) {

}
