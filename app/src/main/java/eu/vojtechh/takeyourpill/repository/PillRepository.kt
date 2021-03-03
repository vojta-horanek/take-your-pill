package eu.vojtechh.takeyourpill.repository

import androidx.lifecycle.LiveData
import eu.vojtechh.takeyourpill.database.PillDao
import eu.vojtechh.takeyourpill.database.ReminderDao
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.PillEntity
import javax.inject.Inject

class PillRepository @Inject constructor(
    private val pillDao: PillDao,
    private val reminderDao: ReminderDao
) {
    fun getAllPills() = pillDao.getAll()
    suspend fun getAllPillsSync() = pillDao.getAllSync()
    fun getAllPillsIncludingDeleted() = pillDao.getAllIncludingDeleted()
    suspend fun getAllPillsIncludingDeletedSync() = pillDao.getAllIncludingDeletedSync()
    fun getPill(pillId: Long) = pillDao.getById(pillId)
    suspend fun getPillSync(pillId: Long) = pillDao.getByIdSync(pillId)

    suspend fun deletePillAndReminder(pill: Pill) {
        pillDao.deletePill(pill.pillEntity)
        reminderDao.delete(pill.reminders)
    }

    suspend fun insertPill(pill: Pill): Long {
        val id = pillDao.insertPill(pill.pillEntity)
        pill.reminders.forEach {
            it.pillId = id
        }
        reminderDao.insert(pill.reminders)
        return id
    }

    suspend fun insertPillReturn(pill: Pill): LiveData<Pill> {
        val id = pillDao.insertPill(pill.pillEntity)
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
        pillDao.updatePill(pill.pillEntity)
        return pill.id
    }

    suspend fun updatePillReturn(pill: Pill): LiveData<Pill> {
        // Delete all reminders based on pill (removes orphaned)
        reminderDao.deleteByPillId(pill.id)
        // Add all reminders (new, updated)
        reminderDao.insert(pill.reminders)
        pillDao.updatePill(pill.pillEntity)
        return pillDao.getById(pill.id)
    }

    suspend fun markPillDeleted(pillEntity: PillEntity) {
        pillEntity.deleted = true
        pillDao.updatePill(pillEntity)
    }
}