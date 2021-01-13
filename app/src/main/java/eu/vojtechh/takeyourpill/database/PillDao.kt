package eu.vojtechh.takeyourpill.database

import androidx.lifecycle.LiveData
import androidx.room.*
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.PillEntity

@Dao
interface PillDao {
    @Transaction
    @Query("SELECT * FROM pill WHERE deleted = 0")
    fun getAll(): LiveData<List<Pill>>

    @Transaction
    @Query("SELECT * FROM pill WHERE deleted = 0")
    fun getAllSync(): List<Pill>

    @Transaction
    @Query("SELECT * FROM pill")
    fun getAllIncludingDeleted(): LiveData<List<Pill>>

    @Transaction
    @Query("SELECT * FROM pill WHERE pillId = (:pillId)")
    fun getById(pillId: Long): LiveData<Pill>

    @Transaction
    @Query("SELECT * FROM pill WHERE pillId = (:pillId)")
    fun getByIdSync(pillId: Long): Pill

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPill(pillEntity: PillEntity): Long

    @Update
    suspend fun updatePill(pillEntity: PillEntity)

    @Delete
    suspend fun deletePill(pillEntity: PillEntity)

}
