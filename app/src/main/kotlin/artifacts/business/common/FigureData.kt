package artifacts.business.common

data class FigureData(
    val name: Name,
    val status: Status,
    val inventory: Inventory,
    val position: Position,
    val equipment: Map<Slot, Equipment>
)
