package artifacts.business

import artifacts.business.common.FigureData
import artifacts.business.common.Item
import artifacts.business.common.Name

class FigureStore(
    private val items: MutableMap<Item.Name, Item.Details>
) {

    private val figureData: MutableMap<Name, FigureData> = mutableMapOf()

    operator fun get(name: Name): FigureData {
        return figureData[name]!!
    }

    operator fun set(name: Name, data: FigureData) {
        figureData[name] = data
    }
}