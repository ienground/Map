package zone.ien.map.data.favorite

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import zone.ien.map.data.Converters

@Database(entities = [Favorite::class], version = 1, exportSchema = false)
@ConstructedBy(FavoriteDatabaseCtor::class)
@TypeConverters(Converters::class)
abstract class FavoriteDatabase: RoomDatabase() {
    abstract fun getDao(): FavoriteDao
    companion object
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object FavoriteDatabaseCtor: RoomDatabaseConstructor<FavoriteDatabase>