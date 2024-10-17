package zone.ien.map.data.history

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import zone.ien.map.data.Converters

@Database(entities = [History::class], version = 1, exportSchema = false)
@ConstructedBy(HistoryDatabaseCtor::class)
@TypeConverters(Converters::class)
abstract class HistoryDatabase: RoomDatabase() {
    abstract fun getDao(): HistoryDao
    companion object
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object HistoryDatabaseCtor: RoomDatabaseConstructor<HistoryDatabase>