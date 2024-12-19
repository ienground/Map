package zone.ien.map.data.history

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers

fun HistoryDatabase.Companion.getDatabaseBuilder(context: Context): RoomDatabase.Builder<HistoryDatabase> {
    val dbFile = context.getDatabasePath("History.db")
    val migration1to2 = object: Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE History_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    label TEXT NOT NULL,
                    address TEXT NOT NULL,
                    latitude REAL NOT NULL,
                    longitude REAL NOT NULL,
                    category TEXT NOT NULL,
                    lastUsedTime INTEGER NOT NULL
                );
            """.trimIndent())
            db.execSQL("""
                INSERT INTO History_new (label, address, latitude, longitude, category, lastUsedTime)
                SELECT label, address, latitude, longitude, CAST(category AS TEXT), lastUsedTime
                FROM History;
            """.trimIndent())
            db.execSQL("DROP TABLE History;");
            db.execSQL("ALTER TABLE History_new RENAME TO History;")
        }
    }

    return Room.databaseBuilder<HistoryDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
        .setQueryCoroutineContext(Dispatchers.IO)
        .addMigrations(migration1to2)
}