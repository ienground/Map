package zone.ien.map.utils

import androidx.compose.ui.text.intl.Locale
import com.sunnychung.lib.multiplatform.kdatetime.KDateTimeFormat
import com.sunnychung.lib.multiplatform.kdatetime.KDuration
import com.sunnychung.lib.multiplatform.kdatetime.KFixedTimeUnit
import com.sunnychung.lib.multiplatform.kdatetime.KInstant
import com.sunnychung.lib.multiplatform.kdatetime.KZoneOffset
import com.sunnychung.lib.multiplatform.kdatetime.KZonedDateTime
import com.sunnychung.lib.multiplatform.kdatetime.serializer.KInstantAsLong
import com.sunnychung.lib.multiplatform.kdatetime.toKZonedDateTime
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toLocalDateTime
import kotlin.math.pow

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
fun KDuration.isAfter(other: KDuration) = hourPart() * 60 + minutePart() > other.hourPart() * 60 + other.minutePart()
fun KDuration.isBefore(other: KDuration) = hourPart() * 60 + minutePart() < other.hourPart() * 60 + other.minutePart()

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
 * Permissions
 */
//expect fun getPermissions(): List<Permissions>
//
//expect object Utils {
//    fun checkPermissions(): Boolean
//    fun getAlarmRingtones(): Map<String, String>
//    fun setAlarmClock(entity: Alarm): KZonedDateTime
//}