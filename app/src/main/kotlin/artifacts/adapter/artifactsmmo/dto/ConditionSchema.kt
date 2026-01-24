package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConditionSchema(
    val code: String,
    val operator: String,
    val value: Int,
) {

}
