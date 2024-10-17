package zone.ien.map.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class DefaultDataStoreProvider: KoinComponent {
    private val context: Context by inject()
    private val dataStore = createDataStoreWithDefaults(migrations = emptyList()) {
        context.filesDir.resolve(DEFAULT_DATASTORE_NAME).absolutePath
    }

    actual fun getDataStore(): DataStore<Preferences> = dataStore
}