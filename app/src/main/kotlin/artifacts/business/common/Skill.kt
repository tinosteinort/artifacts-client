package artifacts.business.common

enum class Skill(val skill: String) {
    WEAPONCRAFTING("weaponcrafting"),
    GEARCRAFTING("gearcrafting"),
    JEWELRYCRAFTING("jewelrycrafting"),
    COOKING("cooking"),
    WOODCUTTING("woodcutting"),
    MINING("mining"),
    ALCHEMY("alchemy");

    companion object {
        fun fromValue(value: String): Skill =
            Skill.entries.single { it.skill == value }
    }
}
