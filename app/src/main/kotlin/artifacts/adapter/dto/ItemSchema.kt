package artifacts.adapter.dto

import kotlinx.serialization.Serializable

@Serializable
data class ItemSchema(
    val name: String,
    val code: String,
    val level: Int,
    val type: String,
    val subtype: String,
    val description: String,
)
