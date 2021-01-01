package eu.vojtechh.takeyourpill.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.vojtechh.takeyourpill.model.History
import eu.vojtechh.takeyourpill.model.HistoryEntity
import eu.vojtechh.takeyourpill.repository.HistoryRepository
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.launch
import java.util.*

class HistoryItemViewModel @ViewModelInject constructor(
    private val pillRepository: PillRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {
    fun getPillById(pillId: Long) = pillRepository.getPill(pillId)
    fun getHistoryForPill(pillId: Long) = historyRepository.getHistoryForPill(pillId)
    fun confirmHistory(item: History) = viewModelScope.launch {
        val historyEntity = HistoryEntity(
            item.historyEntity.id,
            item.historyEntity.reminded,
            Calendar.getInstance(),
            item.historyEntity.pillId
        )
        historyRepository.updateHistoryItem(historyEntity)
    }

    fun markHistoryNotConfirmed(item: History) = viewModelScope.launch {
        val historyEntity = HistoryEntity(
            item.historyEntity.id,
            item.historyEntity.reminded,
            null,
            item.historyEntity.pillId
        )
        historyRepository.updateHistoryItem(historyEntity)
    }
}