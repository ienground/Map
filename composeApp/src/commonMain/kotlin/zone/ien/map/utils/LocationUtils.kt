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
    fun getCurrentLocation()
}

fun LocationUtils.measure(coordinate1: Pair<Double, Double>, coordinate2: Pair<Double, Double>): Int {
    val earthRadius = 6378.137 // Radius of earth in KM
    val latDiff = coordinate2.first * PI / 180 - coordinate1.first * PI / 180
    val lonDiff = coordinate2.second * PI / 180 - coordinate1.second * PI / 180
    val a = sin(latDiff / 2) * sin(latDiff / 2) +
            cos(coordinate1.first * PI / 180) * cos(coordinate2.first / 180) *
            sin(lonDiff / 2) * sin(lonDiff / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val d = earthRadius * c // Distance in km

    return (d * 1000).toInt()
}

fun JsonArray?.toAddress(): String? {
    if (this == null) return null
    val address = find { it.jsonObject["name"]?.jsonPrimitive?.content == "addr" }?.jsonObject
    val roadAddress = find { it.jsonObject["name"]?.jsonPrimitive?.content == "roadaddr" }?.jsonObject
    val areaToken = listOf("area1", "area2", "area3", "area4")
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
    } else if (address != null) {
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
