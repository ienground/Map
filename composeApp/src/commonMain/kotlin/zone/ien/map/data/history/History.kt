package zone.ien.map.data.history

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sunnychung.lib.multiplatform.kdatetime.KDuration
import com.sunnychung.lib.multiplatform.kdatetime.KZonedDateTime
import zone.ien.map.utils.from
import zone.ien.map.utils.now

@Entity(tableName = "History")
data class History(
    val label: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val category: String,
    val lastUsedTime: KZonedDateTime = KZonedDateTime.now()
) {
    @PrimaryKey(autoGenerate = true) var id: Long? = null
}
