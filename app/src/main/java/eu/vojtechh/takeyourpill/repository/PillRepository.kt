package eu.vojtechh.takeyourpill.repository

import eu.vojtechh.takeyourpill.database.PillDao
import eu.vojtechh.takeyourpill.model.Pill
import javax.inject.Inject

class PillRepository @Inject constructor(
    private val pillDao: PillDao
) {
    fun getAllPills() = pillDao.getAll()
    fun getUpcomingPills() = pillDao.getUpcoming()
    fun getPill(pillId: Int) = pillDao.getById(pillId)
    suspend fun getPillAsync(pillId: Int) = pillDao.getByIdAsync(pillId)
    suspend fun deletePill(pill: Pill) = pillDao.delete(pill)
    suspend fun insertPill(pill: Pill) = pillDao.insert(pill)
}