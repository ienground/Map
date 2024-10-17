package zone.ien.map.data

data class QueryResult(
    val title: String = "",
    val category: String = "",
    val telephone: String = "",
    val address: String = "",
    val roadAddress: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)