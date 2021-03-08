package eu.vojtechh.takeyourpill.repository

import eu.vojtechh.takeyourpill.database.HistoryDao
import eu.vojtechh.takeyourpill.model.History
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao
) {
    fun getHistory() = historyDao.getAll()
    fun getHistoryOrderedById() = historyDao.getAllOrderedById()
    fun getHistoryForPill(pillId: Long) = historyDao.getWithPillId(pillId)
    suspend fun getLatestWithPillIdSync(pillId: Long) = historyDao.getLatestWithPillIdSync(pillId)
    suspend fun getByPillIdAndTime(pillId: Long, remindedTime: Long) =
        historyDao.getWithPillIdAndTime(pillId, remindedTime)

    suspend fun updateHistoryItem(history: History) = historyDao.insert(history)
    suspend fun insertHistoryItem(history: History) = historyDao.insert(history)
    suspend fun insertHistories(histories: List<History>) = historyDao.insert(histories)
    suspend fun deleteHistoryItem(history: History) = historyDao.delete(history)
    suspend fun deleteHistoryForPill(pillId: Long) = historyDao.deleteByPillId(pillId)
}