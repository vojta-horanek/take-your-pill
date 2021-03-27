package eu.vojtechh.takeyourpill.database

import androidx.room.*
import eu.vojtechh.takeyourpill.model.History
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY history.reminded DESC")
    fun getEverythingFlow(): Flow<List<History>>

    @Query("SELECT * FROM history ORDER BY pillId ASC")
    fun getEverythingOrderedByIdFlow(): Flow<List<History>>

    @Query("SELECT * FROM history WHERE historyId = (:historyId)")
    fun getWithIdFlow(historyId: Long): Flow<History?>

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
    fun getWithPillIdFlow(pillId: Long): Flow<List<History>>

    @Query(
        """
        SELECT * FROM history
        WHERE 
            history.pillId = (:pillId)
        ORDER BY history.reminded DESC LIMIT 1"""
    )
    suspend fun getLatestWithPillId(pillId: Long): History?

    @Query(
        """
        SELECT * FROM history
        WHERE 
            history.pillId = (:pillId)
        ORDER BY history.reminded DESC LIMIT 1"""
    )
    fun getLatestWithPillIdFlow(pillId: Long): Flow<History?>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: List<History>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: History)

    @Update
    suspend fun update(history: History)

    @Update
    suspend fun update(history: List<History>)

    @Delete
    suspend fun delete(history: History)

    @Delete
    suspend fun delete(history: List<History>)

    @Query("DELETE FROM history WHERE history.pillId = (:pillId)")
    suspend fun deleteWithPillId(pillId: Long)
}