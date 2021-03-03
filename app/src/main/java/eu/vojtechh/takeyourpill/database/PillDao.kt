package eu.vojtechh.takeyourpill.database

import androidx.lifecycle.LiveData
import androidx.room.*
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.PillEntity

@Dao
interface PillDao {
    @Transaction
    @Query("SELECT * FROM pill WHERE deleted = 0 ORDER BY pillId ASC")
    fun getAll(): LiveData<List<Pill>>

    @Transaction
    @Query("SELECT * FROM pill WHERE deleted = 0 ORDER BY pillId ASC")
    suspend fun getAllSync(): List<Pill>

    @Transaction
    @Query("SELECT * FROM pill ORDER BY pillId ASC")
    fun getAllIncludingDeleted(): LiveData<List<Pill>>

    @Transaction
    @Query("SELECT * FROM pill ORDER BY pillId ASC")
    suspend fun getAllIncludingDeletedSync(): List<Pill>

    @Transaction
    @Query("SELECT * FROM pill WHERE pillId = (:pillId)")
    fun getById(pillId: Long): LiveData<Pill>

    @Transaction
    @Query("SELECT * FROM pill WHERE pillId = (:pillId)")
    suspend fun getByIdSync(pillId: Long): Pill

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPill(pillEntity: PillEntity): Long

    @Update
    suspend fun updatePill(pillEntity: PillEntity)

    @Delete
    suspend fun deletePill(pillEntity: PillEntity)

}
