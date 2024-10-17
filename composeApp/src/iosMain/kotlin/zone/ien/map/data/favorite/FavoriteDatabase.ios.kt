package zone.ien.map.data.favorite

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import zone.ien.map.utils.documentDirectory

fun FavoriteDatabase.Companion.getDatabaseBuilder(): RoomDatabase.Builder<FavoriteDatabase> {
    val dbFilePath = "${documentDirectory()}/AlarmDatabase.db"
    return Room.databaseBuilder<FavoriteDatabase>(
        name = dbFilePath,
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
}