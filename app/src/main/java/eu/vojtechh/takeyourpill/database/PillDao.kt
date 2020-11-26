package eu.vojtechh.takeyourpill.database

import androidx.lifecycle.LiveData
import androidx.room.*
import eu.vojtechh.takeyourpill.model.Pill

@Dao
interface PillDao {
    @Query("SELECT * FROM pill")
    fun getAll(): LiveData<List<Pill>>

    // TODO Actually get upcoming
    @Query("SELECT * FROM pill LIMIT 3")
    fun getUpcoming(): LiveData<List<Pill>>

    @Query("SELECT * FROM pill WHERE id = (:pillId)")
    fun getById(pillId: Long): LiveData<Pill>

    @Query("SELECT * FROM pill WHERE id = (:pillId)")
    suspend fun getByIdAsync(pillId: Long): Pill?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pill: Pill): Long

    @Update
    suspend fun update(pill: Pill)

    @Delete
    suspend fun delete(pill: Pill)
}
