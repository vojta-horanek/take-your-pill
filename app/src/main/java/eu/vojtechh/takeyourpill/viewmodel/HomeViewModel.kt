package eu.vojtechh.takeyourpill.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.vojtechh.takeyourpill.klass.hour
import eu.vojtechh.takeyourpill.klass.minute
import eu.vojtechh.takeyourpill.model.History
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.reminder.ReminderUtil
import eu.vojtechh.takeyourpill.repository.HistoryRepository
import eu.vojtechh.takeyourpill.repository.PillRepository
import eu.vojtechh.takeyourpill.repository.ReminderRepository
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    pillRepository: PillRepository,
    private val historyRepository: HistoryRepository,
    private val reminderRepository: ReminderRepository
) : ViewModel() {
    var isReturningFromPillDetails = false
    val allPills = pillRepository.getAllPills()

    var lastPills = listOf<Pill>()

    fun confirmPill(applicationContext: Context, history: History) =
        liveData {
            val reminderTime = Calendar.getInstance().apply {
                clear()
                hour = history.reminded.hour
                minute = history.reminded.minute
            }.timeInMillis

            reminderRepository.getRemindersBasedOnTime(reminderTime)
                .firstOrNull { it.pillId == history.pillId }
                ?.let { reminder ->
                    val confirmIntent = ReminderUtil.getConfirmIntent(
                        applicationContext,
                        reminder.id,
                        history.pillId,
                        history.reminded.timeInMillis
                    )
                    applicationContext.sendBroadcast(confirmIntent)
                    emit(true)
                } ?: emit(false)
        }

    fun addConfirmCards(pills: List<Pill>) = liveData {

        if (pills.isEmpty()) {
            emit(pills)
            return@liveData
        }

        val now = Calendar.getInstance()
        val timeOffset = (30 /* minutes */ * 60 * 1000)

        pills.forEach { pill ->
            val latestHistory = historyRepository.getLatestWithPillIdSync(pill.id)

            pill.closeHistory = null
            latestHistory?.let { history ->
                if (!history.hasBeenConfirmed) {
                    if (now.timeInMillis - history.reminded.timeInMillis <= timeOffset) {
                        pill.closeHistory = history
                    }
                }
            }
            Timber.d(pill.closeHistory.toString())
        }
        emit(pills)
    }

}