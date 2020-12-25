package eu.vojtechh.takeyourpill.database

import androidx.room.*
import eu.vojtechh.takeyourpill.model.History

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history")
    fun getAll(): List<History>

    @Query("SELECT * FROM history WHERE historyId = (:historyId)")
    fun getById(historyId: Long): History

    @Query(
        """
        SELECT * FROM history
        WHERE 
            reminderId = (:reminderId)
        ORDER BY confirmed DESC LIMIT 1"""
    )
    fun getLatestByReminderId(reminderId: Long): History

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminders: List<History>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: History)

    @Update
    suspend fun update(reminder: History)

    @Update
    suspend fun update(reminders: List<History>)

    @Delete
    suspend fun delete(reminder: History)

    @Delete
    suspend fun delete(reminders: List<History>)
}