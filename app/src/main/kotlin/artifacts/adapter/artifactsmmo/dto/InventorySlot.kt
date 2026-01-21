package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
class InventorySlot(
    val slot: Int,
    val code: String,
    val quantity: Int,
)
