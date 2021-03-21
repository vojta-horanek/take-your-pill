package eu.vojtechh.takeyourpill.repository

import eu.vojtechh.takeyourpill.database.PillDao
import eu.vojtechh.takeyourpill.database.ReminderDao
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.PillEntity
import javax.inject.Inject

class PillRepository @Inject constructor(
    private val pillDao: PillDao,
    private val reminderDao: ReminderDao
) {
    fun getAllPills() = pillDao.getEverythingFlow()
    suspend fun getAllPillsSync() = pillDao.getEverything()
    fun getAllPillsIncludingDeleted() = pillDao.getEverythingIncludingDeletedFlow()
    suspend fun getAllPillsIncludingDeletedSync() = pillDao.getEverythingIncludingDeleted()
    fun getPill(pillId: Long) = pillDao.getWithIdFlow(pillId)
    suspend fun getPillSync(pillId: Long) = pillDao.getWitId(pillId)

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