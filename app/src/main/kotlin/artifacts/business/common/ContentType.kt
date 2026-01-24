package artifacts.business.common

enum class ContentType(val type: String) {
    MONSTER("monster"),
    RESOURCE("resource"),
    WORKSHOP("workshop"),
    BANK("bank"),
    GRAND_EXCHANGE("grand_exchange"),
    TASKS_MASTER("tasks_master"),
    NPC("npc");

    companion object {
        fun fromType(type: String): ContentType {
            return ContentType.entries.single { it.type == type }
        }
    }
}