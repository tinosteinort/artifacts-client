package artifacts.adapter.dto

import kotlinx.serialization.Serializable

@Serializable
data class DropSchema(
    val code: String,
    val quantity: Int,
) {
}
