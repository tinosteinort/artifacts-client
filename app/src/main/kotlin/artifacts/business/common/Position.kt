package artifacts.business.common

data class Position(
    val x: Int,
    val y: Int,
    val layer: MapLayer = MapLayer.OVERWORLD
) {
    override fun toString(): String = "[$layer:$x,$y]"
}
