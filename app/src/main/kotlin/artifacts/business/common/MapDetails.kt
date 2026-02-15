package artifacts.business.common

data class MapDetails(
    val position: Position,
    val name: String,
    val skin: String,
    val content: MapContent?,
) {
    fun contains(mob: Mob.Name): Boolean =
        content?.type == ContentType.MOB
                && content.code == mob.name
}
