package eu.vojtechh.takeyourpill.database

import androidx.room.*
import eu.vojtechh.takeyourpill.model.Reminder

@Dao
interface ReminderDao {
    @Query(
        """
        SELECT reminder.pillId, reminder.reminderId, reminder.amount, reminder.time 
        FROM reminder 
        INNER JOIN pill 
            ON reminder.pillId = pill.pillId 
        WHERE deleted = 0 
        ORDER BY time ASC
        """
    )
    suspend fun getEverything(): List<Reminder>

    @Query("SELECT * FROM reminder WHERE reminderId = (:reminderId)")
    suspend fun getWithId(reminderId: Long): Reminder

    @Query(
        """
        SELECT reminder.pillId, reminder.reminderId, reminder.amount, reminder.time 
        FROM reminder 
        INNER JOIN pill 
            ON reminder.pillId = pill.pillId 
        WHERE 
            pill.deleted = 0 AND 
            reminder.time = (:time) 
        ORDER BY time ASC"""
    )
    suspend fun getWithTime(time: Long): List<Reminder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminders: List<Reminder>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder)

    @Update
    suspend fun update(reminder: Reminder)

    @Update
    suspend fun update(reminders: List<Reminder>)

    @Delete
    suspend fun delete(reminder: Reminder)

    @Delete
    suspend fun delete(reminders: List<Reminder>)

    @Query("DELETE FROM reminder WHERE pillId = :pillId")
    suspend fun deleteWithPillId(pillId: Long)

}