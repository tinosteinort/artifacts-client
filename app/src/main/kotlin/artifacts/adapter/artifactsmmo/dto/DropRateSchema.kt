package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class DropRateSchema(
    val code: String,
    val rate: Int,
    val min_quantity: Int,
    val max_quantity: Int,
)
