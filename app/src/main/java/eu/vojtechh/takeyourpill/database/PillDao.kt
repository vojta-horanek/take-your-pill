package eu.vojtechh.takeyourpill.database

import androidx.lifecycle.LiveData
import androidx.room.*
import eu.vojtechh.takeyourpill.model.BasePill
import eu.vojtechh.takeyourpill.model.Pill

@Dao
interface PillDao {
    @Transaction
    @Query("SELECT * FROM pill WHERE deleted = 0")
    fun getAll(): LiveData<List<Pill>>

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
    suspend fun insertPill(pill: BasePill): Long

    @Update
    suspend fun updatePill(pill: BasePill)

    @Delete
    suspend fun deletePill(pill: BasePill)

}
