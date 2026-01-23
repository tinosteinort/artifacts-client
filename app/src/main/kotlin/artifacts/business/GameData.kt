package artifacts.business

import artifacts.business.common.FigureData
import artifacts.business.common.Item
import artifacts.business.common.Name

class GameData(
    private val items: MutableMap<Item.Name, Item.Details>
) {

    private val figureData: MutableMap<Name, FigureData> = mutableMapOf()

    fun figure(name: Name): FigureData {
        return figureData[name]!!
    }

    fun updateFigure(name: Name, data: FigureData) {
        figureData[name] = data
    }
}