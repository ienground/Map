package zone.ien.map.data.history

import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getAll(): Flow<List<History>>

    fun getStream(id: Long): Flow<History?>

    fun getSize(): Flow<Int>

    fun getByCoordinate(latitude: Double, longitude: Double): Flow<History?>

    suspend fun upsert(data: History): Long

    suspend fun delete(data: History)
}