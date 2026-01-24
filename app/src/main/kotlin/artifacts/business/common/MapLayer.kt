package artifacts.business.common

enum class MapLayer(val value: String) {

    INTERIOR("interior"),
    OVERWORLD("overworld"),
    UNDERGROUND("underground");

    companion object {
        fun fromValue(value: String): MapLayer {
            return MapLayer.entries.single { it.value == value }
        }
    }
}