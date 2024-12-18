package zone.ien.map.data.favorite

import kotlinx.coroutines.flow.Flow

class FavoriteOfflineRepository(private val dao: FavoriteDao): FavoriteRepository {
    override fun getAll(): Flow<List<Favorite>> = dao.getAll()

    override fun getStream(id: Long): Flow<Favorite?> = dao.get(id)

    override fun getSize(): Flow<Int> = dao.getSize()

    override fun getByCoordinate(latitude: Double, longitude: Double): Flow<Favorite?> = dao.getByCoordinate(latitude, longitude)

    override suspend fun upsert(data: Favorite) = dao.upsert(data)

    override suspend fun delete(data: Favorite) = dao.delete(data)
}