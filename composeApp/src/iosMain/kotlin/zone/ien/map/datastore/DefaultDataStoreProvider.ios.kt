package zone.ien.map.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import zone.ien.map.utils.documentDirectory

actual class DefaultDataStoreProvider {
    private val dataStore = createDataStoreWithDefaults(migrations = emptyList()) {
        "${documentDirectory()}/$DEFAULT_DATASTORE_NAME"
    }

    actual fun getDataStore(): DataStore<Preferences> = dataStore
}