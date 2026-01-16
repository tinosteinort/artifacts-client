package artifacts.adapter.artifactsmmo.dto

import kotlinx.serialization.Serializable

@Serializable
data class CharacterSchema(
    val name: String,
    val account: String,
    val level: Int,
    val xp: Int,
    val max_xp: Int,
    val gold: Int,
    val hp: Int,
    val max_hp: Int,
    val x: Int,
    val y: Int,
    val map_id: Int,
    val cooldown: Int,
)
