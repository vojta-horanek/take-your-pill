package eu.vojtechh.takeyourpill.receiver

import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.klass.Pref
import eu.vojtechh.takeyourpill.klass.getTimeString
import eu.vojtechh.takeyourpill.reminder.ReminderManager
import eu.vojtechh.takeyourpill.reminder.ReminderUtil
import eu.vojtechh.takeyourpill.repository.PillRepository
import eu.vojtechh.takeyourpill.repository.ReminderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var pillRepository: PillRepository

    @Inject
    lateinit var reminderRepository: ReminderRepository

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        intent.let {
            val reminderTime = it.getLongExtra(Constants.INTENT_EXTRA_REMINDER_TIME, -1L)
            if (reminderTime == -1L) return

            Timber.d("Reminder time: %s", reminderTime.getTimeString())

            GlobalScope.launch(Dispatchers.IO) {
                val reminders = reminderRepository.getRemindersBasedOnTime(reminderTime)
                Timber.d("Reminder count: %s", reminders.count().toString())

                for (reminder in reminders) {
                    val pill = pillRepository.getPillSync(reminder.pillId)

                    ReminderUtil.createStandardReminderNotification(context, pill, reminder)

                    if (Pref.remindAgain) {
                        ReminderManager.setCheckForConfirmation(context, reminder.id)
                    }
                }

                ReminderManager.planNextReminder(context, reminderRepository.getAllReminders())
            }
        }
    }
}