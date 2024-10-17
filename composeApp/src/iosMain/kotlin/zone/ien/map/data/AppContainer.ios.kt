package zone.ien.map.data

import zone.ien.map.data.favorite.FavoriteDatabase
import zone.ien.map.data.favorite.FavoriteOfflineRepository
import zone.ien.map.data.favorite.FavoriteRepository
import zone.ien.map.data.favorite.getDatabaseBuilder
import zone.ien.map.data.history.HistoryDatabase
import zone.ien.map.data.history.HistoryOfflineRepository
import zone.ien.map.data.history.HistoryRepository
import zone.ien.map.data.history.getDatabaseBuilder

actual class AppDataContainer actual constructor() : AppContainer {
    actual override val favoriteRepository: FavoriteRepository by lazy {
        FavoriteOfflineRepository(FavoriteDatabase.getDatabaseBuilder().build().getDao())
    }
    actual override val historyRepository: HistoryRepository by lazy {
        HistoryOfflineRepository(HistoryDatabase.getDatabaseBuilder().build().getDao())
    }
}