package eu.vojtechh.takeyourpill.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import eu.vojtechh.takeyourpill.model.HistoryEntity
import eu.vojtechh.takeyourpill.model.PillEntity
import eu.vojtechh.takeyourpill.model.Reminder

@Database(
    entities = [PillEntity::class, Reminder::class, HistoryEntity::class],
    version = 3
)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun getPillDao(): PillDao
    abstract fun getReminderDao(): ReminderDao
    abstract fun getHistoryDao(): HistoryDao
}