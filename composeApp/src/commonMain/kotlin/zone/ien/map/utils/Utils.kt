package zone.ien.map.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale
import com.sunnychung.lib.multiplatform.kdatetime.KDate
import com.sunnychung.lib.multiplatform.kdatetime.KDateTimeFormat
import com.sunnychung.lib.multiplatform.kdatetime.KDuration
import com.sunnychung.lib.multiplatform.kdatetime.KFixedTimeUnit
import com.sunnychung.lib.multiplatform.kdatetime.KGregorianCalendar.addDays
import com.sunnychung.lib.multiplatform.kdatetime.KInstant
import com.sunnychung.lib.multiplatform.kdatetime.KZoneOffset
import com.sunnychung.lib.multiplatform.kdatetime.KZonedDateTime
import com.sunnychung.lib.multiplatform.kdatetime.serializer.KInstantAsLong
import com.sunnychung.lib.multiplatform.kdatetime.toKZonedDateTime
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toLocalDateTime
import map.composeapp.generated.resources.Res
import map.composeapp.generated.resources.time_format_1hour
import map.composeapp.generated.resources.time_format_1hour_short
import map.composeapp.generated.resources.time_format_1minute
import map.composeapp.generated.resources.time_format_1minute_short
import map.composeapp.generated.resources.time_format_hour
import map.composeapp.generated.resources.time_format_hour_short
import map.composeapp.generated.resources.time_format_minute
import map.composeapp.generated.resources.time_format_minute_short
import org.jetbrains.compose.resources.stringResource
import zone.ien.map.data.favorite.Favorite
import zone.ien.map.data.history.History
import zone.ien.map.ui.screens.home.transport.HistoryDetails
import kotlin.math.pow

/**
 * KDate
 */

fun MyKDate.Companion.now() = KInstant.now().atLocalZoneOffset().toKZonedDateTime().datePart()
fun MyKDate.Companion.Default() = KZonedDateTime.fromMillis(0).datePart()
fun MyKDate.Companion.fromMillis(value: Long) = KZonedDateTime.fromMillis(value).datePart()
fun KDate.timeInMillis() = atTime(KDuration.from(0, 0)).timeInMillis()

fun KDate.plusDay(day: Int) = addDays(day)
fun KDate.minusDay(day: Int) = addDays(-day)
fun KDate.atTime(time: KDuration, timeZone: KZoneOffset = KZoneOffset.local()) = KZonedDateTime(year, month, day, time.hourPart(), time.minutePart(), time.secondPart(), time.millisecondPart(), timeZone)

fun KDate.isEqual(other: KDate) = year == other.year && month == other.month && day == other.day
fun KDate.isAfter(other: KDate, inclusive: Boolean = false) = (year > other.year || (year == other.year && month > other.month) || (year == other.year && month == other.month && day > other.day)) || (isEqual(other) && inclusive)
fun KDate.isBefore(other: KDate, inclusive: Boolean = false) = (year < other.year || (year == other.year && month < other.month) || (year == other.year && month == other.month && day < other.day)) || (isEqual(other) && inclusive)
fun KDate.isBetween(start: KDate, end: KDate, inclusive: Pair<Boolean, Boolean> = Pair(true, true)) = isAfter(start, inclusive = inclusive.first) && isBefore(end, inclusive = inclusive.second)

/**
 * KZonedDateTime
 */
fun KZonedDateTime.Companion.now() = KInstant.now().atLocalZoneOffset().toKZonedDateTime()
fun KZonedDateTime.timeInMillis() = toKInstant().toEpochMilliseconds()
fun KZonedDateTime.Companion.fromMillis(value: Long, timeZone: KZoneOffset? = null): KZonedDateTime {
    return if (timeZone == null) {
        KInstantAsLong(value).atLocalZoneOffset().toKZonedDateTime()
    } else {
        KInstantAsLong(value).atZoneOffset(zoneOffset = timeZone).toKZonedDateTime()
    }
}
val KZonedDateTime.Companion.Default: KZonedDateTime get() = KZonedDateTime.fromMillis(0)

fun KZonedDateTime.dayOfWeek() = Instant.fromEpochMilliseconds(timeInMillis()).toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek.isoDayNumber
fun KZonedDateTime.plusDay(day: Int) = plus(KDuration.of(day, KFixedTimeUnit.Day))
fun KZonedDateTime.minusDay(day: Int) = minus(KDuration.of(day, KFixedTimeUnit.Day))
fun KZonedDateTime.toKDuration() = KDuration.from(hour, minute, second)
fun KZonedDateTime.isEqual(other: KZonedDateTime) = timeInMillis() == other.timeInMillis()
fun KZonedDateTime.isAfter(other: KZonedDateTime, inclusive: Boolean = false) = timeInMillis() > other.timeInMillis() || (isEqual(other) && inclusive)
fun KZonedDateTime.isBefore(other: KZonedDateTime, inclusive: Boolean = false) = timeInMillis() < other.timeInMillis() || (isEqual(other) && inclusive)
fun KZonedDateTime.isBetween(start: KZonedDateTime, end: KZonedDateTime, inclusive: Pair<Boolean, Boolean> = Pair(true, true)) = isAfter(start, inclusive = inclusive.first) && isBefore(end, inclusive = inclusive.second)
/**
 * KDuration
 */
fun KDuration.Companion.from(hourOfDay: Int = 0, minute: Int = 0, second: Int = 0) = of(hourOfDay * 3600 + minute * 60 + second, KFixedTimeUnit.Second)
fun KDuration.Companion.now() = KZonedDateTime.now().let { KDuration.from(it.hour, it.minute, it.second) }
fun KDuration.int() = hourPart() * 60 + minutePart()
fun KDuration.Companion.fromInt(value: Int) = KDuration.from(value / 60, value % 60)

fun KDuration.plusHour(hour: Int) = plus(KDuration.of(hour, KFixedTimeUnit.Hour))
fun KDuration.minusHour(hour: Int) = minus(KDuration.of(hour, KFixedTimeUnit.Hour))
fun KDuration.plusMinute(minute: Int) = plus(KDuration.of(minute, KFixedTimeUnit.Minute))
fun KDuration.minusMinute(minute: Int) = minus(KDuration.of(minute, KFixedTimeUnit.Minute))

fun KDuration.isEqual(other: KDuration) = hourPart() * 60 + minutePart() == other.hourPart() * 60 + other.minutePart()
fun KDuration.isAfter(other: KDuration, inclusive: Boolean = false) = hourPart() * 60 + minutePart() > other.hourPart() * 60 + other.minutePart() || (isEqual(other) && inclusive)
fun KDuration.isBefore(other: KDuration, inclusive: Boolean = false) = hourPart() * 60 + minutePart() < other.hourPart() * 60 + other.minutePart() || (isEqual(other) && inclusive)
fun KDuration.isBetween(start: KDuration, end: KDuration, inclusive: Pair<Boolean, Boolean> = Pair(true, true)) = isAfter(start, inclusive = inclusive.first) && isBefore(end, inclusive = inclusive.second)

/**
 * KDateTimeFormat
 */
fun KDateTimeFormat.format(time: KZonedDateTime) = format(time.toKInstant())

/**
 * Locale
 */
val Locale.Companion.KOREA: Locale get() = Locale("ko-KR")

fun getNextRepeatDay(time: KZonedDateTime, alarmTime: KDuration, data: Int): Int {
    val repeatDay = arrayListOf<Boolean>()
    val today = time.dayOfWeek() - 1 // 금요일 5
    var nextRepeatDay = -2
    val range = if (time.toKDuration().isBefore(alarmTime)) 0..6 else 1..7

    for (i in 0 until 7) {
        repeatDay.add(data.and(2.0.pow(6 - i).toInt()) != 0)
    }

    for (i in range) {
        val result = repeatDay[(today + i) % 7]
        if (result) {
            nextRepeatDay = (today + i) % 7
            break
        }
    }

    return nextRepeatDay + 1
}
/**
 * Time
 */
@Composable
fun Int.toFormattedTime(): String {
    val hours = this / 60
    val minutes = this % 60
    val time = arrayListOf<String>()

    if (hours == 1) time.add(stringResource(Res.string.time_format_1hour_short))
    else if (hours > 0) time.add(stringResource(Res.string.time_format_hour_short, hours))
    if (minutes == 1) time.add(stringResource(Res.string.time_format_1minute_short))
    else if (minutes > 0) time.add(stringResource(Res.string.time_format_minute_short, minutes))

    return time.joinToString(" ")
}


/**
 * List
 */
fun <T> List<T>.safeSubList(fromIndex: Int, toIndex: Int): List<T> = subList(fromIndex, toIndex.coerceAtMost(this.size))

enum class HistoryGroup {
    TODAY, YESTERDAY, WEEK, MONTH, YEAR, OLD
}
fun List<HistoryDetails>.groupByTime(): List<Pair<HistoryGroup, List<HistoryDetails>>> {
    val result = mutableMapOf<HistoryGroup, List<HistoryDetails>>()
    result[HistoryGroup.TODAY] = (filter { it.lastUsedTime.datePart().isEqual(MyKDate.now()) })
    result[HistoryGroup.YESTERDAY] = (filter { it.lastUsedTime.datePart().isEqual(MyKDate.now().minusDay(1)) })
    result[HistoryGroup.WEEK] = (filter { it.lastUsedTime.datePart().isBetween(MyKDate.now().minusDay(7), MyKDate.now().minusDay(2)) })
    result[HistoryGroup.MONTH] = (filter { it.lastUsedTime.datePart().isBetween(MyKDate.now().minusDay(30), MyKDate.now().minusDay(8)) })
    result[HistoryGroup.YEAR] = (filter { it.lastUsedTime.datePart().isBetween(MyKDate.now().minusDay(365), MyKDate.now().minusDay(31)) })
    result[HistoryGroup.OLD] = (filter { it.lastUsedTime.datePart().isBefore(MyKDate.now().minusDay(365)) })


    return result.toList().sortedBy { it.first.ordinal }
}

/**
 * Text
 */
fun String.removeBoldTag() = replace(Regex("<b>|</b>"), "")

/**
 * Permissions
 */
//expect fun getPermissions(): List<Permissions>
//
//expect object Utils {
//    fun checkPermissions(): Boolean
//    fun getAlarmRingtones(): Map<String, String>
//    fun setAlarmClock(entity: Alarm): KZonedDateTime
//}