package eu.vojtechh.takeyourpill.database

import androidx.lifecycle.LiveData
import androidx.room.*
import eu.vojtechh.takeyourpill.model.History
import eu.vojtechh.takeyourpill.model.HistoryEntity

@Dao
interface HistoryDao {
    @Transaction
    @Query("SELECT * FROM history")
    fun getAll(): LiveData<List<History>>

    @Transaction
    @Query("SELECT * FROM history WHERE historyId = (:historyId)")
    fun getById(historyId: Long): LiveData<History>

    @Transaction
    @Query(
        """
        SELECT * FROM history
        WHERE 
            reminderId = (:reminderId)
        ORDER BY confirmed DESC LIMIT 1"""
    )
    fun getLatestByReminderId(reminderId: Long): LiveData<History>

    @Transaction
    @Query(
        """
        SELECT history.historyId, history.reminderId, history.confirmed, history.reminded FROM history
        INNER JOIN reminder
        ON history.reminderId = reminder.reminderId
        WHERE 
            reminder.pillId = (:pillId)
        ORDER BY history.reminded DESC"""
    )
    fun getHistoryByPillId(pillId: Long): LiveData<List<History>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historyEntity: List<HistoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historyEntityItem: HistoryEntity)

    @Update
    suspend fun update(historyEntityItem: HistoryEntity)

    @Update
    suspend fun update(historyEntity: List<HistoryEntity>)

    @Delete
    suspend fun delete(historyEntityItem: HistoryEntity)

    @Delete
    suspend fun delete(historyEntity: List<HistoryEntity>)
}