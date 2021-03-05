package eu.vojtechh.takeyourpill.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.vojtechh.takeyourpill.model.History
import eu.vojtechh.takeyourpill.repository.HistoryRepository
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val pillRepository: PillRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {
    var isReturningFromPillDetails = false

    fun confirmPill(history: History) = viewModelScope.launch(Dispatchers.IO) {
        history.confirmed = Calendar.getInstance()
        historyRepository.updateHistoryItem(history)
    }

    fun getPills() = liveData {
        val pills = pillRepository.getAllPillsSync()

        val now = Calendar.getInstance()
        val timeOffset = (30 /* minutes */ * 60 * 1000)

        pills.forEach { pill ->
            val latestHistory = historyRepository.getLatestWithPillIdSync(pill.id)

            latestHistory?.let { history ->
                if (!history.hasBeenConfirmed) {
                    if (now.timeInMillis - history.reminded.timeInMillis <= timeOffset) {
                        pill.closeHistory = history
                    }
                }
            }
        }
        emit(pills)
    }

}