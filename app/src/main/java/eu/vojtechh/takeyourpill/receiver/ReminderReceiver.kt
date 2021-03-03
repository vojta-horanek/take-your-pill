package eu.vojtechh.takeyourpill.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.klass.Pref
import eu.vojtechh.takeyourpill.model.History
import eu.vojtechh.takeyourpill.reminder.ReminderManager
import eu.vojtechh.takeyourpill.reminder.ReminderUtil
import eu.vojtechh.takeyourpill.repository.HistoryRepository
import eu.vojtechh.takeyourpill.repository.PillRepository
import eu.vojtechh.takeyourpill.repository.ReminderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var pillRepository: PillRepository

    @Inject
    lateinit var reminderRepository: ReminderRepository

    @Inject
    lateinit var historyRepository: HistoryRepository

    override fun onReceive(context: Context, intent: Intent) {
        intent.let {

            val reminderId = it.getLongExtra(Constants.INTENT_EXTRA_REMINDER_ID, -1L)
            if (reminderId == -1L) {
                Timber.e("Haven't received a reminder ID, exiting...")
                return
            }

            Timber.d("Received reminder ID: %s", reminderId)

            GlobalScope.launch(Dispatchers.IO) {

                val reminder = reminderRepository.getReminder(reminderId)
                val pill = pillRepository.getPillSync(reminder.pillId)

                // If pill is active, create a notification, insert history and schedule a check
                if (pill.options.isActive()) {

                    val todayCalendar = reminder.getTodayCalendar()

                    ReminderUtil.createReminderNotification(context, pill, reminder)

                    val history = History(
                        pillId = pill.id,
                        reminded = todayCalendar,
                        amount = reminder.amount
                    )
                    historyRepository.insertHistoryItem(history)

                    // Schedule a check reminder if enabled
                    if (Pref.remindAgain) {
                        ReminderManager.createCheckAlarm(
                            context,
                            reminder.id,
                            todayCalendar.timeInMillis
                        )
                    }

                } else { // If pill is not active, check if it is finite
                    if (pill.options.isFinite()) {
                        // If the pill is finite and inactive, it means we will not be reminding it anymore
                        return@launch
                    }
                }

                // Plan next reminder and update the pill in db with new options
                val newPill = ReminderManager.planNextPillReminder(context, pill)
                pillRepository.updatePill(newPill)

            }
        }
    }
}