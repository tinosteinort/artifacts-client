package artifacts.business.result

sealed class InitItemsResult {

    class Success(
        val total: Int,
        val page: Int,
        val pageSize: Int,
        val pages: Int,
    ) : InitItemsResult()
}
