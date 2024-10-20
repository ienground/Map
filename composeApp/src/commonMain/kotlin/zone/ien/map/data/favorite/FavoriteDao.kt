package zone.ien.map.data.favorite

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Upsert
    suspend fun upsert(data: Favorite): Long

    @Delete
    suspend fun delete(data: Favorite)

    @Query("SELECT * FROM Favorite WHERE id = :id")
    fun get(id: Long): Flow<Favorite>

    @Query("SELECT * FROM Favorite ORDER BY lastUsedTime DESC")
    fun getAll(): Flow<List<Favorite>>

    @Query("SELECT COUNT(*) FROM Favorite")
    fun getSize(): Flow<Int>
}