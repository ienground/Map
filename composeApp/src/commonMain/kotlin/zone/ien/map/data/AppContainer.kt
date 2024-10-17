package zone.ien.map.data

import zone.ien.map.data.favorite.FavoriteRepository
import zone.ien.map.data.history.HistoryRepository

interface AppContainer {
    val favoriteRepository: FavoriteRepository
    val historyRepository: HistoryRepository
}
expect class AppDataContainer(): AppContainer {
    override val favoriteRepository: FavoriteRepository
    override val historyRepository: HistoryRepository
}