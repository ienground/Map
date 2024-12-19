import zone.ien.map.utils.DecimalFormat
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatTest {
    @Test
    fun formatNumber() {
        val numberFormat = DecimalFormat()
        val formattedNumber = numberFormat.format(1234567.89)
        assertEquals("1,234,567.89", formattedNumber)

    }

}