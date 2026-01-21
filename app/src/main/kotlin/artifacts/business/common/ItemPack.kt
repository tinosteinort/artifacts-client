package artifacts.business.common

data class ItemPack<T : Item>(
    val item: T,
    val quantity: Int,
)
