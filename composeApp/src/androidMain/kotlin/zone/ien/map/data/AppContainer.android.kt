package zone.ien.map.data

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import zone.ien.map.data.favorite.FavoriteDatabase
import zone.ien.map.data.favorite.FavoriteOfflineRepository
import zone.ien.map.data.favorite.FavoriteRepository
import zone.ien.map.data.favorite.getDatabaseBuilder
import zone.ien.map.data.history.HistoryDatabase
import zone.ien.map.data.history.HistoryOfflineRepository
import zone.ien.map.data.history.HistoryRepository
import zone.ien.map.data.history.getDatabaseBuilder

actual class AppDataContainer: AppContainer, KoinComponent {
    private val context: Context by inject()

    actual override val favoriteRepository: FavoriteRepository by lazy {
        FavoriteOfflineRepository(FavoriteDatabase.getDatabaseBuilder(context).build().getDao())
    }

    actual override val historyRepository: HistoryRepository by lazy {
        HistoryOfflineRepository(HistoryDatabase.getDatabaseBuilder(context).build().getDao())
    }
}