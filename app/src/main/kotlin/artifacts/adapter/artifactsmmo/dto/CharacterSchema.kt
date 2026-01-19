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
    val weapon_slot: String,
    val rune_slot: String,
    val shield_slot: String,
    val helmet_slot: String,
    val body_armor_slot: String,
    val leg_armor_slot: String,
    val boots_slot: String,
    val ring1_slot: String,
    val ring2_slot: String,
    val amulet_slot: String,
    val artifact1_slot: String,
    val artifact2_slot: String,
    val artifact3_slot: String,
    val utility1_slot: String,
    val utility1_slot_quantity: Int,
    val utility2_slot: String,
    val utility2_slot_quantity: Int,
    val bag_slot: String,
)
