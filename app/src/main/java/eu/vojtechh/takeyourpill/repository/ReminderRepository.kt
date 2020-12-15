package eu.vojtechh.takeyourpill.repository

import eu.vojtechh.takeyourpill.database.ReminderDao
import javax.inject.Inject

class ReminderRepository @Inject constructor(
    private val reminderDao: ReminderDao
) {
    fun getReminder(reminderId: Long) = reminderDao.getById(reminderId)
    fun getAllReminders() = reminderDao.getAll()
    fun getRemindersBasedOnTime(time: Long) = reminderDao.getBasedOnTime(time)
}