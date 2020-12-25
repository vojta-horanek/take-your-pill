package eu.vojtechh.takeyourpill.repository

import eu.vojtechh.takeyourpill.database.HistoryDao
import eu.vojtechh.takeyourpill.model.History
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao
) {
    fun getHistoryItem(historyId: Long) = historyDao.getById(historyId)
    fun getHistory() = historyDao.getAll()
    fun getLatestByReminderId(reminderId: Long) = historyDao.getLatestByReminderId(reminderId)
    suspend fun updateHistoryItem(history: History) = historyDao.update(history)
    suspend fun insertHistoryItem(history: History) = historyDao.insert(history)
    suspend fun deleteHistoryItem(history: History) = historyDao.delete(history)
}