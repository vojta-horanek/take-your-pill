package eu.vojtechh.takeyourpill.repository

import eu.vojtechh.takeyourpill.database.ReminderDao
import javax.inject.Inject

class ReminderRepository @Inject constructor(
    private val reminderDao: ReminderDao
) {
    suspend fun getReminder(reminderId: Long) = reminderDao.getById(reminderId)
    suspend fun getAllReminders() = reminderDao.getAll()
    suspend fun getRemindersBasedOnTime(time: Long) = reminderDao.getBasedOnTime(time)
}