package eu.vojtechh.takeyourpill.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import eu.vojtechh.takeyourpill.model.Pill

@Database(
    entities = [Pill::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class PillDatabase : RoomDatabase() {
    abstract fun getPillDao(): PillDao
}