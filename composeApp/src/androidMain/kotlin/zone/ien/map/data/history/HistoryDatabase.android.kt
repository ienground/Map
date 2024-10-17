package zone.ien.map.data.history

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers

fun HistoryDatabase.Companion.getDatabaseBuilder(context: Context): RoomDatabase.Builder<HistoryDatabase> {
    val dbFile = context.getDatabasePath("History.db")
    return Room.databaseBuilder<HistoryDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
        .setQueryCoroutineContext(Dispatchers.IO)
}