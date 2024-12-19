package zone.ien.map.data.history

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import zone.ien.map.data.favorite.Favorite

@Dao
interface HistoryDao {
    @Upsert
    suspend fun upsert(data: History): Long

    @Delete
    suspend fun delete(data: History)

    @Query("SELECT * FROM History WHERE id = :id")
    fun get(id: Long): Flow<History>

    @Query("SELECT * FROM History ORDER BY lastUsedTime")
    fun getAll(): Flow<List<History>>

    @Query("SELECT COUNT(*) FROM History")
    fun getSize(): Flow<Int>

    @Query("SELECT * FROM History WHERE latitude = :latitude AND longitude = :longitude")
    fun getByCoordinate(latitude: Double, longitude: Double): Flow<History?>

}