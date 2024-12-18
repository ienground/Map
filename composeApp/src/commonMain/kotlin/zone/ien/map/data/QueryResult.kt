package zone.ien.map.data

data class QueryResult(
    val title: String = "",
    val categories: List<String> = listOf(),
    val address: String = "",
    val roadAddress: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)