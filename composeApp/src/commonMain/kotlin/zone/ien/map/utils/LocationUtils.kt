package zone.ien.map.utils

import nl.jacobras.humanreadable.HumanReadable
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

fun Int.diffToString(): String {
    return if (this < 1000) "${this}m"
    else if (this < 10000) "${HumanReadable.number(this / 1000.0, 1)}km"
    else "${this / 1000}km"
}
