package eu.vojtechh.takeyourpill.database

import androidx.lifecycle.LiveData
import androidx.room.*
import eu.vojtechh.takeyourpill.model.BaseHistory
import eu.vojtechh.takeyourpill.model.History

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
        SELECT * FROM history
        INNER JOIN reminder
        ON history.reminderId = reminder.reminderId
        WHERE 
            reminder.pillId = (:pillId)
        ORDER BY confirmed DESC"""
    )
    fun getHistoryByPillId(pillId: Long): LiveData<List<History>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: List<BaseHistory>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historyItem: BaseHistory)

    @Update
    suspend fun update(historyItem: BaseHistory)

    @Update
    suspend fun update(history: List<BaseHistory>)

    @Delete
    suspend fun delete(historyItem: BaseHistory)

    @Delete
    suspend fun delete(history: List<BaseHistory>)
}