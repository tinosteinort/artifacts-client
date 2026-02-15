package artifacts.business.common


sealed class Mob(val name: String) {

    class Name(name: String) : Mob(name)

    class Details(
        name: String,
        val description: String,
        val level: Int,
        val type: MobType,
        val hp: Int,
        val drops: Set<Item.Name>,
    ) : Mob(name)

    override fun toString() = name
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Mob

        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
