package eu.vojtechh.takeyourpill.database

import androidx.lifecycle.LiveData
import androidx.room.*
import eu.vojtechh.takeyourpill.model.BasePill
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.Reminder

@Dao
interface PillDao {
    @Transaction
    @Query("SELECT * FROM pill WHERE deleted = 0")
    fun getAll(): LiveData<List<Pill>>

    @Transaction
    @Query("SELECT * FROM pill WHERE pillId = (:pillId)")
    fun getById(pillId: Long): LiveData<Pill>

    @Query("SELECT reminder.pillId, reminder.reminderId, reminder.amount, reminder.calendar FROM reminder INNER JOIN pill ON reminder.pillId = pill.pillId WHERE deleted = 0 ORDER BY calendar ASC")
    fun getAllReminders(): List<Reminder>

    @Query("SELECT * FROM reminder WHERE reminderId = (:reminderId)")
    fun getReminderById(reminderId: Long): Reminder

    @Query("SELECT * FROM pill WHERE pill.pillId = (SELECT reminder.pillId FROM reminder WHERE reminder.reminderId = (:reminderId))")
    fun getPillByReminderId(reminderId: Long): Pill

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPill(pill: BasePill): Long

    @Update
    suspend fun updatePill(pill: BasePill)

    @Delete
    suspend fun deletePill(pill: BasePill)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<Reminder>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder)

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Update
    suspend fun updateReminders(reminders: List<Reminder>)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminders(reminders: List<Reminder>)

    @Query("DELETE FROM reminder WHERE pillId = :pillId")
    suspend fun deleteRemindersByPillId(pillId: Long)


}
