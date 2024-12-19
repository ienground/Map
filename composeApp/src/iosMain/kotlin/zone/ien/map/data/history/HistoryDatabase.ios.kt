package zone.ien.map.data.history

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import zone.ien.map.utils.documentDirectory

fun HistoryDatabase.Companion.getDatabaseBuilder(): RoomDatabase.Builder<HistoryDatabase> {
    val dbFilePath = "${documentDirectory()}/History.db"
    val migration1to2 = object: Migration(1, 2) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL("""
                CREATE TABLE History_new (
                    label TEXT NOT NULL,
                    address TEXT NOT NULL,
                    latitude REAL NOT NULL,
                    longitude REAL NOT NULL,
                    category TEXT NOT NULL, -- category 타입 변경
                    lastUsedTime TEXT NOT NULL -- KZonedDateTime을 TEXT로 저장
                );
            """.trimIndent())
            connection.execSQL("""
                INSERT INTO History_new (label, address, latitude, longitude, category, lastUsedTime)
                SELECT label, address, latitude, longitude, CAST(category AS TEXT), lastUsedTime
                FROM History;
            """.trimIndent())
            connection.execSQL("DROP TABLE History;")
            connection.execSQL("ALTER TABLE History_new RENAME TO History;")
        }
    }

    return Room.databaseBuilder<HistoryDatabase>(
        name = dbFilePath,
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .addMigrations(migration1to2)
}