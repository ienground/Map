package zone.ien.map.utils

expect class DecimalFormat() {
    fun format(number: Double): String
    fun format(number: Int): String
}