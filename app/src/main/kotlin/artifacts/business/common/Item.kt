package artifacts.business.common


sealed class Item(val value: String) {

    class Name(value: String) : Item(value)
    class Details(
        value: String,
        level: Int,
        type: ItemType,
    ) : Item(value)
}
