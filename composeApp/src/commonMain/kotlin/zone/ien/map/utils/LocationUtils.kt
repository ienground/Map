package zone.ien.map.utils

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import nl.jacobras.humanreadable.HumanReadable
import zone.ien.map.TAG
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

expect object LocationUtils {
    fun getCurrentLocation(
        setLatLng: (MapLatLng) -> Unit
    )
}

data class MapPointF(
    val x: Float,
    val y: Float
)

data class MapLatLng(
    var latitude: Double,
    var longitude: Double
)

fun LocationUtils.measure(coordinate1: MapLatLng, coordinate2: MapLatLng): Int {
    val earthRadius = 6378.137 // Radius of earth in KM
    val latDiff = coordinate2.latitude * PI / 180 - coordinate1.latitude * PI / 180
    val lonDiff = coordinate2.longitude * PI / 180 - coordinate1.longitude * PI / 180
    val a = sin(latDiff / 2) * sin(latDiff / 2) +
            cos(coordinate1.latitude * PI / 180) * cos(coordinate2.latitude / 180) *
            sin(lonDiff / 2) * sin(lonDiff / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val d = earthRadius * c // Distance in km

    return (d * 1000).toInt()
}

fun JsonArray?.toRoadAddress(): String? {
    if (this == null) return null
    val roadAddress = find { it.jsonObject["name"]?.jsonPrimitive?.content == "roadaddr" }?.jsonObject
    val roadAreaToken = listOf("area1", "area2")

    return if (roadAddress != null) {
        val result = arrayListOf<String>()
        for (token in roadAreaToken) {
            roadAddress["region"]?.jsonObject?.get(token)?.jsonObject?.get("name")?.jsonPrimitive?.content?.let { if (it.isNotEmpty()) result.add(it) }
        }
        roadAddress["land"]?.jsonObject?.get("name")?.jsonPrimitive?.content?.let { if (it.isNotEmpty()) result.add(it) }
        val number1 = roadAddress["land"]?.jsonObject?.get("number1")?.jsonPrimitive?.content ?: ""
        val number2 = roadAddress["land"]?.jsonObject?.get("number2")?.jsonPrimitive?.content ?: ""

        if (number2.isBlank()) result.add(number1)
        else result.add("${number1}-${number2}")

        result.joinToString(" ")
    } else ""
}

fun JsonArray?.toAddress(): String? {
    if (this == null) return null
    val address = find { it.jsonObject["name"]?.jsonPrimitive?.content == "addr" }?.jsonObject
    val areaToken = listOf("area1", "area2", "area3", "area4")

    return if (address != null) {
        val result = arrayListOf<String>()
        for (token in areaToken) {
            address["region"]?.jsonObject?.get(token)?.jsonObject?.get("name")?.jsonPrimitive?.content?.let { if (it.isNotEmpty()) result.add(it) }
        }
        address["land"]?.jsonObject?.get("name")?.jsonPrimitive?.content?.let { if (it.isNotEmpty()) result.add(it) }
        val number1 = address["land"]?.jsonObject?.get("number1")?.jsonPrimitive?.content ?: ""
        val number2 = address["land"]?.jsonObject?.get("number2")?.jsonPrimitive?.content ?: ""

        if (number2.isBlank()) result.add(number1)
        else result.add("${number1}-${number2}")

        result.joinToString(" ")
    } else ""
}

fun Int.diffToString(): String {
    return if (this < 1000) "${this}m"
    else if (this < 10000) "${HumanReadable.number(this / 1000.0, 1)}km"
    else "${this / 1000}km"
}

// 지구 반경 (미터 단위)
const val EARTH_RADIUS = 6371000.0

fun toRadians(deg: Double): Double = deg / 180.0 * PI

// Haversine 공식을 사용하여 두 좌표 간의 거리 계산
fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val dLat = toRadians(lat2 - lat1)
    val dLon = toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) + cos(toRadians(lat1)) * cos(toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return EARTH_RADIUS * c
}

// 일정 간격(interval)으로 좌표 추출하는 함수
fun extractRepresentativeCoordinates(path: List<MapLatLng>, interval: Double): List<MapLatLng> {
    if (path.isEmpty()) return emptyList()

    val representativeCoords = mutableListOf(path[0]) // 첫 번째 좌표는 무조건 포함
    var accumulatedDistance = 0.0

    for (i in 1 until path.size) {
        val previousCoord = path[i - 1]
        val currentCoord = path[i]

        val distance = haversine(previousCoord.latitude, previousCoord.longitude, currentCoord.latitude, currentCoord.longitude)
        accumulatedDistance += distance

        if (accumulatedDistance >= interval) {
            representativeCoords.add(currentCoord)
            accumulatedDistance = 0.0 // 거리 초기화
        }
    }

    return representativeCoords
}

fun extractRepresentativeCoordinatesByCount(path: List<MapLatLng>, count: Int): List<MapLatLng> {
    if (path.isEmpty() || count <= 0) return emptyList()

    // 결과 리스트에 첫 번째 좌표를 추가
    val representativeCoords = mutableListOf(path[0])

    if (path.size == 1 || count == 1) {
        return representativeCoords // 경로가 하나뿐이거나 대표 좌표가 하나인 경우
    }

    val step = (path.size - 1).toDouble() / (count - 1) // 각 대표 좌표 간의 간격 계산

    for (i in 1 until count) {
        val index = (i * step).toInt().coerceAtMost(path.size - 1) // 인덱스를 경로 크기 범위 내로 제한
        representativeCoords.add(path[index])
    }

    return representativeCoords
}
