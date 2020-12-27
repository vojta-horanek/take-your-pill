package eu.vojtechh.takeyourpill.repository

import androidx.lifecycle.LiveData
import eu.vojtechh.takeyourpill.database.PillDao
import eu.vojtechh.takeyourpill.database.ReminderDao
import eu.vojtechh.takeyourpill.model.BasePill
import eu.vojtechh.takeyourpill.model.Pill
import javax.inject.Inject

class PillRepository @Inject constructor(
    private val pillDao: PillDao,
    private val reminderDao: ReminderDao
) {
    fun getAllPills() = pillDao.getAll()
    fun getAllPillsIncludingDeleted() = pillDao.getAllIncludingDeleted()
    fun getPill(pillId: Long) = pillDao.getById(pillId)
    fun getPillSync(pillId: Long) = pillDao.getByIdSync(pillId)

    suspend fun deletePillAndReminder(pill: Pill) {
        pillDao.deletePill(pill.pill)
        reminderDao.delete(pill.reminders)
    }

    suspend fun insertPill(pill: Pill): Long {
        val id = pillDao.insertPill(pill.pill)
        pill.reminders.forEach {
            it.pillId = id
        }
        reminderDao.insert(pill.reminders)
        return id
    }

    suspend fun insertPillReturn(pill: Pill): LiveData<Pill> {
        val id = pillDao.insertPill(pill.pill)
        pill.reminders.forEach {
            it.pillId = id
        }
        reminderDao.insert(pill.reminders)
        return pillDao.getById(id)
    }

    suspend fun updatePill(pill: Pill): Long {
        // Delete all reminders based on pill (removes orphaned)
        reminderDao.deleteByPillId(pill.id)
        // Add all reminders (new, updated)
        reminderDao.insert(pill.reminders)
        pillDao.updatePill(pill.pill)
        return pill.id
    }

    suspend fun updatePillReturn(pill: Pill): LiveData<Pill> {
        // Delete all reminders based on pill (removes orphaned)
        reminderDao.deleteByPillId(pill.id)
        // Add all reminders (new, updated)
        reminderDao.insert(pill.reminders)
        pillDao.updatePill(pill.pill)
        return pillDao.getById(pill.id)
    }

    suspend fun markPillDeleted(pill: BasePill) {
        pill.deleted = true
        pillDao.updatePill(pill)
    }
}