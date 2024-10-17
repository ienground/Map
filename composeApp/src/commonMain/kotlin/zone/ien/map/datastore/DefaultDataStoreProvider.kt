package zone.ien.map.datastore

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import org.koin.core.qualifier.named

internal const val DEFAULT_DATASTORE_NAME = "datastore/settings.preferences_pb"
val DEFAULT_DATASTORE = named("default_datastore")

internal fun createDataStoreWithDefaults(
    migrations: List<DataMigration<Preferences>> = emptyList(),
    producePath: () -> String
) = PreferenceDataStoreFactory.createWithPath(
    corruptionHandler = null,
    migrations = migrations,
    produceFile = {
        producePath().toPath()
    }
)

expect class DefaultDataStoreProvider() {
    fun getDataStore(): DataStore<Preferences>
}