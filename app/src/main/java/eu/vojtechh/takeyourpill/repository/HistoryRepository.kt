package eu.vojtechh.takeyourpill.repository

import eu.vojtechh.takeyourpill.database.HistoryDao
import eu.vojtechh.takeyourpill.model.BaseHistory
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao
) {
    fun getHistoryItem(historyId: Long) = historyDao.getById(historyId)
    fun getHistory() = historyDao.getAll()
    fun getLatestByReminderId(reminderId: Long) = historyDao.getLatestByReminderId(reminderId)
    fun getHistoryForPill(pillId: Long) = historyDao.getHistoryByPillId(pillId)
    suspend fun updateHistoryItem(baseHistory: BaseHistory) = historyDao.update(baseHistory)
    suspend fun insertHistoryItem(baseHistory: BaseHistory) = historyDao.insert(baseHistory)
    suspend fun deleteHistoryItem(baseHistory: BaseHistory) = historyDao.delete(baseHistory)
}