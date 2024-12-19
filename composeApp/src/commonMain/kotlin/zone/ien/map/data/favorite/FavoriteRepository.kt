package zone.ien.map.data.favorite

import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAll(): Flow<List<Favorite>>

    fun getStream(id: Long): Flow<Favorite?>

    fun getSize(): Flow<Int>

    fun getByCoordinate(latitude: Double, longitude: Double): Flow<Favorite?>

    fun getByCoordinateOnlyEtc(latitude: Double, longitude: Double): Flow<Favorite?>

    fun getByType(type: Int): Flow<Favorite?>

    suspend fun upsert(data: Favorite): Long

    suspend fun delete(data: Favorite)
}