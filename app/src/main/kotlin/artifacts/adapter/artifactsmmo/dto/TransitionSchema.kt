package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransitionSchema(
    val x: Int,
    val y: Int,
    val layer: String,
    val conditions: Set<ConditionSchema>,
) {

}
