package eu.vojtechh.takeyourpill.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import eu.vojtechh.takeyourpill.model.History
import eu.vojtechh.takeyourpill.model.PillEntity
import eu.vojtechh.takeyourpill.model.Reminder

@Database(
    entities = [PillEntity::class, Reminder::class, History::class],
    version = 6
)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun getPillDao(): PillDao
    abstract fun getReminderDao(): ReminderDao
    abstract fun getHistoryDao(): HistoryDao
}