package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class UnequipResponseDto(
    val data: EquipRequestSchema
)
