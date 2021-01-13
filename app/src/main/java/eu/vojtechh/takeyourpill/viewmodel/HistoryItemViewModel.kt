package eu.vojtechh.takeyourpill.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.vojtechh.takeyourpill.model.History
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
        val historyEntity = History(
            item.id,
            item.reminded,
            Calendar.getInstance(),
            item.pillId
        )
        historyRepository.updateHistoryItem(historyEntity)
    }

    fun markHistoryNotConfirmed(item: History) = viewModelScope.launch {
        val historyEntity = History(
            item.id,
            item.reminded,
            null,
            item.pillId
        )
        historyRepository.updateHistoryItem(historyEntity)
    }

    fun setHistoryConfirmTime(item: History, newConfirmTime: Calendar) = viewModelScope.launch {
        val historyEntity = History(
            item.id,
            item.reminded,
            newConfirmTime,
            item.pillId
        )
        historyRepository.updateHistoryItem(historyEntity)
    }

    fun deleteHistory(item: History) = viewModelScope.launch {
        historyRepository.deleteHistoryItem(item)
    }
}