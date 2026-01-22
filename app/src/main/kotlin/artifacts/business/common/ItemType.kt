package artifacts.business.common

enum class ItemType(val code: String) {

    CONSUMABLE("consumable"),
    UNKNOWN("unknown");

    companion object {
        fun fromCode(code: String): ItemType {
            return ItemType.entries.singleOrNull() { it.code == code } ?: ItemType.UNKNOWN
        }
    }
}
