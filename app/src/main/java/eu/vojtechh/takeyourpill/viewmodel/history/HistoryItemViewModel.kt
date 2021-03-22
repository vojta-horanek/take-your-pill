package eu.vojtechh.takeyourpill.viewmodel.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.vojtechh.takeyourpill.model.History
import eu.vojtechh.takeyourpill.repository.HistoryRepository
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HistoryItemViewModel @Inject constructor(
    private val pillRepository: PillRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {
    fun getPillById(pillId: Long) = pillRepository.getPillFlow(pillId).asLiveData()
    val namedHistory = historyRepository.getHistoryFlow().map { historyList ->
        val pills = pillRepository.getAllPillsIncludingDeleted()
        return@map historyList.onEach { history ->
            history.pillName = pills.find {
                it.id == history.pillId
            }?.name ?: "N/A"
        }
    }.asLiveData()

    fun getHistoryForPill(pillId: Long) =
        historyRepository.getHistoryForPillFlow(pillId).asLiveData()

    fun confirmHistory(item: History) = viewModelScope.launch {
        val historyEntity =
            History(item.id, item.reminded, Calendar.getInstance(), item.amount, item.pillId)
        historyRepository.updateHistoryItem(historyEntity)
    }

    fun markHistoryNotConfirmed(item: History) = viewModelScope.launch {
        val historyEntity = History(item.id, item.reminded, null, item.amount, item.pillId)
        historyRepository.updateHistoryItem(historyEntity)
    }

    fun setHistoryConfirmTime(item: History, newConfirmTime: Calendar) = viewModelScope.launch {
        val historyEntity =
            History(item.id, item.reminded, newConfirmTime, item.amount, item.pillId)
        historyRepository.updateHistoryItem(historyEntity)
    }

    fun setHistoryAmount(item: History, newAmount: String) = viewModelScope.launch {
        val historyEntity = History(item.id, item.reminded, item.confirmed, newAmount, item.pillId)
        historyRepository.updateHistoryItem(historyEntity)
    }

    fun deleteHistory(item: History) = viewModelScope.launch {
        historyRepository.deleteHistoryItem(item)
    }

    /**
     * Deletes the history
     * If pill is marked "deleted" it deletes the pill thus deleting its history as well
     * If pill is not marked "deleted" it only deletes all the history entities with the pills id
     *
     * @return LiveData Boolean - true means pill deleted
     *                            false means only history deleted
     */
    fun deletePillHistory(pillId: Long) = liveData(Dispatchers.IO) {
        val pill = pillRepository.getPill(pillId)
        if (pill.deleted) {
            pillRepository.deletePillAndReminder(pill)
            emit(true)
        } else {
            historyRepository.deleteHistoryForPill(pillId)
            emit(false)

        }
    }
}