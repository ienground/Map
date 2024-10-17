package zone.ien.map.data.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sunnychung.lib.multiplatform.kdatetime.KZonedDateTime
import zone.ien.map.data.history.History
import zone.ien.map.utils.now

@Entity(tableName = "Favorite")
data class Favorite(
    val label: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val type: Int = -1,
    val registerTime: KZonedDateTime = KZonedDateTime.now(),
    val lastUsedTime: KZonedDateTime = KZonedDateTime.now()
) {
    @PrimaryKey(autoGenerate = true) var id: Long? = null

    object Type {
        const val ETC = -1
        const val HOME = 0
        const val OFFICE = 1
        const val SCHOOL = 2
    }
}
