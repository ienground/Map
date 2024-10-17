package zone.ien.map.data.history

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import zone.ien.map.utils.documentDirectory

fun HistoryDatabase.Companion.getDatabaseBuilder(): RoomDatabase.Builder<HistoryDatabase> {
    val dbFilePath = "${documentDirectory()}/AlarmDatabase.db"
    return Room.databaseBuilder<HistoryDatabase>(
        name = dbFilePath,
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
}