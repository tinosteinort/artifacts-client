package artifacts.adapter.artifactsmmo

class Page<T>(
    val items: Set<T>,
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val pages: Int,
)
