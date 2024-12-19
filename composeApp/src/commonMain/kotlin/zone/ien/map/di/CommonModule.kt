package zone.ien.map.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.SavedStateHandle
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import zone.ien.map.data.AppContainer
import zone.ien.map.data.AppDataContainer
import zone.ien.map.datastore.DEFAULT_DATASTORE
import zone.ien.map.datastore.DefaultDataStoreProvider
import zone.ien.map.ui.screens.home.HomeViewModel
import zone.ien.map.ui.screens.home.profile.edit.ProfileEditViewModel
import zone.ien.map.ui.screens.home.profile.list.ProfileListViewModel
import zone.ien.map.ui.screens.home.transport.TransportViewModel
import zone.ien.map.ui.screens.permissions.PermissionsViewModel

val commonModule = module {
    single<AppContainer> { AppDataContainer() }
    single<DataStore<Preferences>>(DEFAULT_DATASTORE) { DefaultDataStoreProvider().getDataStore() }

    viewModel { HomeViewModel() }
    viewModel { PermissionsViewModel() }
    viewModel { val container: AppContainer by inject(); TransportViewModel(container.favoriteRepository, container.historyRepository) }
    viewModel { val container: AppContainer by inject(); ProfileListViewModel(container.favoriteRepository) }
    viewModel { (savedStateHandle: SavedStateHandle) -> val container: AppContainer by inject(); ProfileEditViewModel(savedStateHandle, container.favoriteRepository) }

}