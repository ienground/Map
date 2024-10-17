package zone.ien.map.data

import androidx.room.TypeConverter
import com.sunnychung.lib.multiplatform.kdatetime.KDuration
import com.sunnychung.lib.multiplatform.kdatetime.KInstant
import com.sunnychung.lib.multiplatform.kdatetime.KZonedDateTime
import com.sunnychung.lib.multiplatform.kdatetime.serializer.KInstantAsLong
import com.sunnychung.lib.multiplatform.kdatetime.toKZonedDateTime
import kotlinx.serialization.json.*
import zone.ien.map.utils.from
import zone.ien.map.utils.fromInt
import zone.ien.map.utils.fromMillis
import zone.ien.map.utils.int
import zone.ien.map.utils.timeInMillis
import zone.ien.map.utils.fromInt

class Converters {
    @TypeConverter
    fun fromIntToKDuration(value: Int?): KDuration? = value?.let { KDuration.fromInt(it) }
    
    @TypeConverter
    fun fromKDurationToInt(value: KDuration?): Int? = value?.int()
    
    @TypeConverter
    fun fromLongToDateTime(value: Long?): KZonedDateTime? = value?.let { KZonedDateTime.fromMillis(it) }
    
    @TypeConverter
    fun fromDateTimeToLong(value: KZonedDateTime?): Long? = value?.timeInMillis()
}