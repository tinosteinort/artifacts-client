package artifacts.business.common


sealed class Resource(val name: String) {

    class Name(name: String) : Resource(name)

    class Details(
        name: String,
        val level: Int,
        val skill: ResourceSkill,
        val drops: Set<Item.Name>,
    ) : Resource(name)

    override fun toString() = name
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Resource

        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
