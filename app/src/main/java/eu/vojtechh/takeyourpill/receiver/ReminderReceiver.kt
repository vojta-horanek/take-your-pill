package eu.vojtechh.takeyourpill.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.klass.Pref
import eu.vojtechh.takeyourpill.klass.getTimeString
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
            val reminderTime = it.getLongExtra(Constants.INTENT_EXTRA_REMINDER_TIME, -1L)
            if (reminderTime == -1L) return

            Timber.d("Reminder time: %s", reminderTime.getTimeString())

            GlobalScope.launch(Dispatchers.IO) {
                // We probably don't need this anymore since we are creating reminders on pill bases
                val reminders = reminderRepository.getRemindersBasedOnTime(reminderTime)
                Timber.d("Reminder count: %s", reminders.count().toString())

                for (reminder in reminders) {
                    val pill = pillRepository.getPillSync(reminder.pillId)

                    if (pill.options.isActive()) {

                        val remindedCalendar = reminder.getCalendarWithTodayDate()
                        ReminderUtil.createReminderNotification(context, pill, reminder)

                        val history = History(
                            pillId = pill.id,
                            reminded = remindedCalendar,
                            amount = reminder.amount
                        )
                        historyRepository.insertHistoryItem(history)
                        if (Pref.remindAgain) {
                            ReminderManager.setCheckForConfirmation(
                                context,
                                reminder.id,
                                remindedCalendar.timeInMillis
                            )
                        }
                    } else {
                        if (pill.options.isFinite()) {
                            return@launch
                        }
                    }

                    val newPill = ReminderManager.planNextPillReminder(context, pill)
                    pillRepository.updatePill(newPill)
                }
            }
        }
    }
}