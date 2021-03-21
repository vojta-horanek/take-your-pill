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
    fun getAllPillsWithHistoryFlow(pillId: Long?) = pillDao.getEverythingFlow().map { pillList ->
        pillId?.let { id ->
            pillList.find { it.id == id }?.let { pill ->
                getLatestHistoryForPill(pill)
            }
        } ?: run {
            pillList.forEach { pill ->
                getLatestHistoryForPill(pill)
            }
        }
        pillList
    }

    private suspend fun getLatestHistoryForPill(pill: Pill) {
        val now = Calendar.getInstance()
        val timeOffset = (30 /* minutes */ * 60 * 1000)
        val latestHistory = historyDao.getLatestWithPillId(pill.id)
        pill.closeHistory = null
        latestHistory?.let { history ->
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
        val id = pillDao.insertPillEntity(pill.pillEntity)
        pill.reminders.forEach {
            it.pillId = id
        }
        reminderDao.insert(pill.reminders)
        return id
    }

    suspend fun insertPillReturn(pill: Pill): Pill {
        val id = pillDao.insertPillEntity(pill.pillEntity)
        pill.reminders.forEach {
            it.pillId = id
        }
        reminderDao.insert(pill.reminders)
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