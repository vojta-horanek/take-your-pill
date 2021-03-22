package eu.vojtechh.takeyourpill.repository

import eu.vojtechh.takeyourpill.database.HistoryDao
import eu.vojtechh.takeyourpill.database.PillDao
import eu.vojtechh.takeyourpill.database.ReminderDao
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.PillEntity
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class PillRepository @Inject constructor(
    private val pillDao: PillDao,
    private val reminderDao: ReminderDao,
    private val historyDao: HistoryDao
) {
    fun getAllPillsWithHistoryFlow() = pillDao.getEverythingFlow().map { pillList ->
        pillList.onEach { pill -> addLatestHistoryToPill(pill) }
    }

    private suspend fun addLatestHistoryToPill(pill: Pill) {
        val now = Calendar.getInstance()
        val timeOffset = (30 /* minutes */ * 60 * 1000)
        pill.closeHistory = null
        historyDao.getLatestWithPillId(pill.id)?.let { history ->
            if (!history.hasBeenConfirmed) {
                if (now.timeInMillis - history.reminded.timeInMillis <= timeOffset) {
                    pill.closeHistory = history
                }
            }
        }
    }

    suspend fun getAllPills() = pillDao.getEverything()

    suspend fun getAllPillsIncludingDeleted() = pillDao.getEverythingIncludingDeleted()

    fun getPillFlow(pillId: Long) = pillDao.getWithIdFlow(pillId)

    suspend fun getPill(pillId: Long) = pillDao.getWitId(pillId)

    suspend fun deletePillAndReminder(pill: Pill) {
        pillDao.deletePillEntity(pill.pillEntity)
        reminderDao.delete(pill.reminders)
    }

    suspend fun insertPill(pill: Pill): Long {
        val pillId = pillDao.insertPillEntity(pill.pillEntity)
        reminderDao.insert(pill.reminders.onEach { it.pillId = pillId })
        return pillId
    }

    suspend fun insertPillReturn(pill: Pill): Pill {
        val id = pillDao.insertPillEntity(pill.pillEntity)
        reminderDao.insert(pill.reminders.onEach { it.pillId = id })
        return pillDao.getWitId(id)
    }

    suspend fun updatePill(pill: Pill): Long {
        // Delete all reminders based on pill (removes orphaned)
        reminderDao.deleteWithPillId(pill.id)
        // Add all reminders (new, updated)
        reminderDao.insert(pill.reminders)
        pillDao.updatePillEntity(pill.pillEntity)
        return pill.id
    }

    suspend fun updatePillReturn(pill: Pill): Pill {
        // Delete all reminders based on pill (removes orphaned)
        reminderDao.deleteWithPillId(pill.id)
        // Add all reminders (new, updated)
        reminderDao.insert(pill.reminders)
        pillDao.updatePillEntity(pill.pillEntity)
        return pillDao.getWitId(pill.id)
    }

    suspend fun markPillDeleted(pillEntity: PillEntity) {
        // Delete all reminders based on pill (removes orphaned)
        reminderDao.deleteWithPillId(pillEntity.id)
        pillEntity.deleted = true
        pillDao.updatePillEntity(pillEntity)
    }
}