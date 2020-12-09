package eu.vojtechh.takeyourpill.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import eu.vojtechh.takeyourpill.model.BasePill
import eu.vojtechh.takeyourpill.model.Reminder

@Database(
    entities = [BasePill::class, Reminder::class],
    version = 17
)
@TypeConverters(Converters::class)
abstract class PillDatabase : RoomDatabase() {
    abstract fun getPillDao(): PillDao
}