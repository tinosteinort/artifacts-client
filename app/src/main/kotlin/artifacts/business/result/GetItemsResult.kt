package artifacts.business.result

import artifacts.business.common.Item

sealed class GetItemsResult {

    class Success(
        val items: Map<Item.Name, Item.Details>,
        val total: Int,
        val page: Int,
        val pageSize: Int,
        val pages: Int,
    ) : GetItemsResult()
}
