package eu.vojtechh.takeyourpill.repository

import eu.vojtechh.takeyourpill.database.HistoryDao
import eu.vojtechh.takeyourpill.model.HistoryEntity
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao
) {
    fun getHistoryItem(historyId: Long) = historyDao.getById(historyId)
    fun getHistory() = historyDao.getAll()
    fun getHistoryForPill(pillId: Long) = historyDao.getHistoryByPillId(pillId)
    suspend fun getByPillIdAndTime(pillId: Long, remindedTime: Long) =
        historyDao.getByPillIdAndTime(pillId, remindedTime)

    suspend fun updateHistoryItem(historyEntity: HistoryEntity) = historyDao.update(historyEntity)
    suspend fun insertHistoryItem(historyEntity: HistoryEntity) = historyDao.insert(historyEntity)
    suspend fun deleteHistoryItem(historyEntity: HistoryEntity) = historyDao.delete(historyEntity)
}