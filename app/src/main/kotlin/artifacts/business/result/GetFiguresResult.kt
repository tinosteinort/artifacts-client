package artifacts.business.result

import artifacts.business.common.FigureData

sealed class GetFiguresResult {

    class Success(
        val figures: Set<FigureData>
    ) : GetFiguresResult()
}
