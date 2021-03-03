package eu.vojtechh.takeyourpill.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.vojtechh.takeyourpill.model.Reminder
import eu.vojtechh.takeyourpill.repository.HistoryRepository
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
        pillRepository: PillRepository,
        private val historyRepository: HistoryRepository
) : ViewModel() {
    val allPills = pillRepository.getAllPills()
    var isReturningFromPillDetails = false

    fun confirmPill(reminder: Reminder) = viewModelScope.launch(Dispatchers.IO) {
        // We can use today millis because this function will be only executed few minutes after or before reminder
        // FIXME If user confirms 5 minutes before midnight
        // FIXME Update doesn't work for non existent row => when executed before reminder
        historyRepository.getByPillIdAndTime(reminder.pillId, reminder.getTodayMillis())
                ?.let { history ->
                history.confirmed = Calendar.getInstance()
                historyRepository.updateHistoryItem(history)
            }

    }
}