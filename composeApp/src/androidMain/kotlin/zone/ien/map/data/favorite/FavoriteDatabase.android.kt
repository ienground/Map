package zone.ien.map.data.favorite

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers

fun FavoriteDatabase.Companion.getDatabaseBuilder(context: Context): RoomDatabase.Builder<FavoriteDatabase> {
    val dbFile = context.getDatabasePath("Favorite.db")
    return Room.databaseBuilder<FavoriteDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
        .setQueryCoroutineContext(Dispatchers.IO)
}