package zone.ien.map.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.dsl.module
import zone.ien.map.data.AppContainer
import zone.ien.map.data.AppDataContainer
import zone.ien.map.datastore.DEFAULT_DATASTORE
import zone.ien.map.datastore.DefaultDataStoreProvider

val commonModule = module {
//    single { NetworkListener(get()) }
    single<AppContainer> { AppDataContainer() }
    single<DataStore<Preferences>>(DEFAULT_DATASTORE) { DefaultDataStoreProvider().getDataStore() }
}