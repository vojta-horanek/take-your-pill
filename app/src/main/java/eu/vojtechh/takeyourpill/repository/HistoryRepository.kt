package eu.vojtechh.takeyourpill.repository

import eu.vojtechh.takeyourpill.database.HistoryDao
import eu.vojtechh.takeyourpill.model.History
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao
) {
    fun getHistoryItem(historyId: Long) = historyDao.getWithId(historyId)
    fun getHistory() = historyDao.getAll()
    fun getHistoryOrderedById() = historyDao.getAllOrderedById()
    fun getHistoryForPill(pillId: Long) = historyDao.getWithPillId(pillId)
    suspend fun getByPillIdAndTime(pillId: Long, remindedTime: Long) =
        historyDao.getWithPillIdAndTime(pillId, remindedTime)

    suspend fun updateHistoryItem(historyEntity: History) = historyDao.insert(historyEntity)
    suspend fun insertHistoryItem(historyEntity: History) = historyDao.insert(historyEntity)
    suspend fun insertHistories(histories: List<History>) = historyDao.insert(histories)
    suspend fun deleteHistoryItem(historyEntity: History) = historyDao.delete(historyEntity)
    suspend fun deleteHistoryForPill(pillId: Long) = historyDao.deleteByPillId(pillId)
}