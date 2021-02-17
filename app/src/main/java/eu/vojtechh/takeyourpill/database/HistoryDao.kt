package eu.vojtechh.takeyourpill.database

import androidx.lifecycle.LiveData
import androidx.room.*
import eu.vojtechh.takeyourpill.model.History

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY history.reminded DESC")
    fun getAll(): LiveData<List<History>>

    @Query("SELECT * FROM history ORDER BY pillId ASC")
    fun getAllOrderedById(): LiveData<List<History>>

    @Query("SELECT * FROM history ORDER BY pillId ASC")
    suspend fun getAllOrderedByIdSync(): List<History>

    @Query("SELECT * FROM history WHERE historyId = (:historyId)")
    fun getWithId(historyId: Long): LiveData<History?>

    @Query(
            """
        SELECT * FROM history
        WHERE
            pillId = (:pillId) AND
            reminded = (:remindedTime)
        ORDER BY confirmed DESC LIMIT 1"""
    )
    suspend fun getWithPillIdAndTime(pillId: Long, remindedTime: Long): History?

    @Query(
        """
        SELECT * FROM history
        WHERE 
            history.pillId = (:pillId)
        ORDER BY history.reminded DESC"""
    )
    fun getWithPillId(pillId: Long): LiveData<List<History>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historyEntity: List<History>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historyEntityItem: History)

    @Update
    suspend fun update(historyEntityItem: History)

    @Update
    suspend fun update(historyEntity: List<History>)

    @Delete
    suspend fun delete(historyEntityItem: History)

    @Delete
    suspend fun delete(historyEntity: List<History>)

    @Query("DELETE FROM history WHERE history.pillId = (:pillId)")
    suspend fun deleteByPillId(pillId: Long)
}