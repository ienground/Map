package zone.ien.map.data.history

import kotlinx.coroutines.flow.Flow
import zone.ien.map.data.favorite.Favorite

class HistoryOfflineRepository(private val dao: HistoryDao): HistoryRepository {
    override fun getAll(): Flow<List<History>> = dao.getAll()

    override fun getStream(id: Long): Flow<History?> = dao.get(id)

    override fun getSize(): Flow<Int> = dao.getSize()

    override fun getByCoordinate(latitude: Double, longitude: Double): Flow<History?> = dao.getByCoordinate(latitude, longitude)

    override suspend fun upsert(data: History) = dao.upsert(data)

    override suspend fun delete(data: History) = dao.delete(data)
}

/*
class AlarmOfflineRepository(private val dao: AlarmDao): AlarmRepository {
    val list = arrayListOf(
        Alarm(if (Locale.getDefault() == Locale.KOREA) "일어나기" else "Wake up", 7 * 60 + 30, true, 0b1111111, "", "default_sound", true, false, -1, arrayListOf(Pair(10, true), Pair(15, true))).apply { id = 0 },
        Alarm(if (Locale.getDefault() == Locale.KOREA) "수업 준비" else "Prepare for class", 13 * 60 + 30, false, 0b1111111, "", "default_sound", true, true, -1, arrayListOf(Pair(10, true))).apply { id = 1 },
    )

    override fun getAllStream(): Flow<List<Alarm>> = flow { emit(list) }

    override fun getIsEnabledStream(): Flow<List<Alarm>> = flow { emit(list.filter { it.isEnabled }) }

    override fun getStream(id: Long): Flow<Alarm?> = flow { emit(list.find { it.id == id }) }

    override fun getSize(): Flow<Int> = flow { list.size }

    override suspend fun upsert(data: Alarm): Long {
        list.removeIf { it.id == data.id }
        list.add(data)

        return data.id ?: -1
    }

    override suspend fun delete(data: Alarm) {
        list.removeIf { it.id == data.id }
    }
}

 */