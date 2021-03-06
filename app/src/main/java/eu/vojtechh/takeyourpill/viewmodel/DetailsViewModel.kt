package eu.vojtechh.takeyourpill.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.vojtechh.takeyourpill.model.History
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.PillEntity
import eu.vojtechh.takeyourpill.model.Reminder
import eu.vojtechh.takeyourpill.repository.HistoryRepository
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val pillRepository: PillRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {
    fun getPillById(pillId: Long) = pillRepository.getPill(pillId)

    fun deletePillWithHistory(pill: Pill) =
            viewModelScope.launch(Dispatchers.IO) { pillRepository.deletePillAndReminder(pill) }

    fun deletePill(pillEntity: PillEntity) =
            viewModelScope.launch(Dispatchers.IO) { pillRepository.markPillDeleted(pillEntity) }

    lateinit var pill: Pill

    private val _reminders = MutableLiveData(listOf<Reminder>())

    val reminders = Transformations.map(_reminders) {
        pill.reminders = it.sortedBy { rem -> rem.time.time }.toMutableList()
        pill.reminders
    }

    fun setReminders(reminders: List<Reminder>) {
        _reminders.value = reminders
    }

    fun getLatestHistory(validTimeOffset: Boolean) = liveData {
        val now = Calendar.getInstance()
        val timeOffset = (30 /* minutes */ * 60 * 1000)

        val latestHistory = historyRepository.getLatestWithPillIdSync(pill.id)

        latestHistory?.let { history ->
            if (!history.hasBeenConfirmed) {
                if (validTimeOffset) {
                    if (now.timeInMillis - history.reminded.timeInMillis <= timeOffset) {
                        emit(history)
                    } else {
                        emit(null)
                    }
                } else {
                    emit(history)
                }
            } else {
                emit(null)
            }
        } ?: emit(null)
    }

    fun confirmPill(history: History) = viewModelScope.launch(Dispatchers.IO) {
        history.confirmed = Calendar.getInstance()
        historyRepository.updateHistoryItem(history)
    }
}