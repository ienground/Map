package zone.ien.map.utils

actual class DecimalFormat {
    actual fun format(number: Double): String {
        val df = java.text.DecimalFormat()
        df.isGroupingUsed = true
        df.maximumFractionDigits = 2
        df.isDecimalSeparatorAlwaysShown = false
        return df.format(number)
    }

    actual fun format(number: Int): String {
        val df = java.text.DecimalFormat()
        df.isGroupingUsed = true
        df.maximumFractionDigits = 2
        df.isDecimalSeparatorAlwaysShown = false
        return df.format(number)
    }
}