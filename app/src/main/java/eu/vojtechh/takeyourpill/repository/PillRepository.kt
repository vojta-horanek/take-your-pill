package eu.vojtechh.takeyourpill.repository

import eu.vojtechh.takeyourpill.database.PillDao
import eu.vojtechh.takeyourpill.model.BasePill
import eu.vojtechh.takeyourpill.model.Pill
import javax.inject.Inject

class PillRepository @Inject constructor(
    private val pillDao: PillDao
) {
    fun getAllPills() = pillDao.getAll()
    fun getPill(pillId: Long) = pillDao.getById(pillId)
    fun getReminder(reminderId: Long) = pillDao.getReminderById(reminderId)
    fun getAllReminders() = pillDao.getAllReminders()
    fun getPillByReminderId(reminderId: Long) = pillDao.getPillByReminderId(reminderId)
    suspend fun deletePillAndReminder(pill: Pill) {
        pillDao.deletePill(pill.pill)
        pillDao.deleteReminders(pill.reminders)
    }

    suspend fun insertPill(pill: Pill): Long {
        val id = pillDao.insertPill(pill.pill)
        pill.reminders.forEach {
            it.pillId = id
        }
        pillDao.insertReminders(pill.reminders)
        return id
    }

    suspend fun updatePill(pill: Pill): Long {
        // Delete all reminders based on pill (removes orphaned)
        pillDao.deleteRemindersByPillId(pill.id)
        // Add all reminders (new, updated)
        pillDao.insertReminders(pill.reminders)
        pillDao.updatePill(pill.pill)
        return pill.id
    }

    suspend fun markPillDeleted(pill: BasePill) {
        pill.deleted = true
        pillDao.updatePill(pill)
    }
}