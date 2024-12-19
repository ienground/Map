package zone.ien.map.ui.screens.home.profile.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import zone.ien.map.data.favorite.FavoriteRepository
import zone.ien.map.ui.screens.home.transport.TransportFavoriteUiStateList
import zone.ien.map.ui.screens.home.transport.toFavoriteDetails

class ProfileListViewModel(
    private val favoriteRepository: FavoriteRepository,
): ViewModel() {
    val favoriteUiStateList: StateFlow<TransportFavoriteUiStateList> =
        favoriteRepository.getAll().map { TransportFavoriteUiStateList(it.map { favorite ->
            favorite.toFavoriteDetails()
        }, isInitialized = true) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TransportFavoriteUiStateList()
            )

    init {

    }


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}