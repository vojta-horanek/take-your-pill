package eu.vojtechh.takeyourpill.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import eu.vojtechh.takeyourpill.model.Pill

@Dao
interface PillDao {
    @Query("SELECT * FROM pill")
    fun getAll(): LiveData<List<Pill>>

    // TODO Actually get upcoming
    @Query("SELECT * FROM pill LIMIT 3")
    fun getUpcoming(): LiveData<List<Pill>>

    @Query("SELECT * FROM pill WHERE id = (:pillId)")
    fun getById(pillId: Int): LiveData<Pill>

    @Insert
    suspend fun insert(pill: Pill)

    @Delete
    suspend fun delete(pill: Pill)
}
