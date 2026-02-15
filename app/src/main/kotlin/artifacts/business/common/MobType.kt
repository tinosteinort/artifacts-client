package artifacts.business.common

enum class MobType(val code: String) {

    NORMAL("normal"),
    ELITE("elite"),
    BOSS("boss");

    companion object {
        fun fromCode(code: String): MobType {
            return entries.single() { it.code == code }
        }
    }
}
