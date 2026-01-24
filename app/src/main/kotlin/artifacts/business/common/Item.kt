package artifacts.business.common


sealed class Item(val value: String) {

    class Name(value: String) : Item(value)

    class Details(
        value: String,
        level: Int,
        type: ItemType,
        craftInfo: CraftInfo?,
    ) : Item(value)

    override fun toString() = value
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Item

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
