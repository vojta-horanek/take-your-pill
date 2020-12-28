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
            pillId = (:pillId)
        ORDER BY confirmed DESC LIMIT 1"""
    )
    fun getLatestByPillId(pillId: Long): LiveData<History>

    @Transaction
    @Query(
        """
        SELECT * FROM history
        WHERE
            pillId = (:pillId) AND
            reminded = (:remindedTime)
        ORDER BY confirmed DESC LIMIT 1"""
    )
    suspend fun getByPillIdAndTime(pillId: Long, remindedTime: Long): History

    @Transaction
    @Query(
        """
        SELECT * FROM history
        WHERE 
            history.pillId = (:pillId)
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