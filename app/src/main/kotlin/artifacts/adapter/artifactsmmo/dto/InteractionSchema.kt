package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class InteractionSchema(
    val content: MapContentSchema?,
    val transition: TransitionSchema?,
)
