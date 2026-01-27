package artifacts.business.common

enum class ResourceSkill(val skill: String) {
    FISHING("fishing"),
    WOODCUTTING("woodcutting"),
    MINING("mining"),
    ALCHEMY("alchemy");

    companion object {
        fun fromValue(value: String): ResourceSkill =
            entries.single { it.skill == value }
    }
}
