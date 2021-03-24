package eu.vojtechh.takeyourpill.viewmodel

import android.content.Context
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.vojtechh.takeyourpill.klass.hour
import eu.vojtechh.takeyourpill.klass.minute
import eu.vojtechh.takeyourpill.model.History
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.PillEntity
import eu.vojtechh.takeyourpill.model.Reminder
import eu.vojtechh.takeyourpill.reminder.ReminderUtil
import eu.vojtechh.takeyourpill.repository.HistoryRepository
import eu.vojtechh.takeyourpill.repository.PillRepository
import eu.vojtechh.takeyourpill.repository.ReminderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val pillRepository: PillRepository,
    private val reminderRepository: ReminderRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {
    fun getPillById(pillId: Long) = pillRepository.getPillFlow(pillId).asLiveData()

    fun deletePillWithHistory(pill: Pill) =
        viewModelScope.launch(Dispatchers.IO) { pillRepository.deletePillAndReminder(pill) }

    fun deletePill(pillEntity: PillEntity) =
        viewModelScope.launch(Dispatchers.IO) { pillRepository.markPillDeleted(pillEntity) }

    fun getLastReminded(pillId: Long) =
        historyRepository.getLatestWithPillIdFlow(pillId).asLiveData()

    lateinit var pill: Pill

    private val _reminders = MutableLiveData(listOf<Reminder>())

    val reminders = Transformations.map(_reminders) {
        pill.reminders = it.sortedBy { rem -> rem.time.time }.toMutableList()
        pill.reminders
    }

    fun setReminders(reminders: List<Reminder>) {
        _reminders.value = reminders
    }

    fun getLatestHistory(respectTimeOffset: Boolean) =
        historyRepository.getLatestWithPillIdFlow(pill.id).map { history ->
            val now = Calendar.getInstance()
            val timeOffset = (30 /* minutes */ * 60 * 1000)

            if (history != null &&
                !history.hasBeenConfirmed &&
                (!respectTimeOffset ||
                        now.timeInMillis - history.reminded.timeInMillis <= timeOffset)
            ) history else null
        }.asLiveData()

    fun confirmPill(context: Context, history: History) =
        liveData(Dispatchers.Default) {
            val reminderTime = Calendar.getInstance().apply {
                clear()
                hour = history.reminded.hour
                minute = history.reminded.minute
            }.timeInMillis

            reminderRepository.getRemindersBasedOnTime(reminderTime)
                .firstOrNull { it.pillId == history.pillId }
                ?.let { reminder ->
                    val confirmIntent = ReminderUtil.getConfirmIntent(
                        context,
                        reminder.id,
                        history.pillId,
                        history.reminded.timeInMillis
                    )
                    context.sendBroadcast(confirmIntent)
                    delay(200) // Wait for broadcast to finish
                    emit(true)
                } ?: emit(false)
        }

    private val loadedDataCount = MutableLiveData(0)
    val loadedData: LiveData<Int>
        get() = loadedDataCount

    fun loadedData() {
        loadedDataCount.value = loadedDataCount.value?.plus(1)
    }

    fun finishedLoading() {
        loadedDataCount.value = 0
    }
}