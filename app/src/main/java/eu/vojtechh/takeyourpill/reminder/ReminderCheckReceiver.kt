package eu.vojtechh.takeyourpill.reminder

import android.content.Context
import android.content.Intent
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.klass.HiltBroadcastReceiver
import eu.vojtechh.takeyourpill.repository.PillRepository
import eu.vojtechh.takeyourpill.repository.ReminderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ReminderCheckReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var pillRepository: PillRepository

    @Inject
    lateinit var reminderRepository: ReminderRepository

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        intent.let {

            val reminderId = intent.getLongExtra(Constants.INTENT_EXTRA_REMINDER_ID, -1L)
            if (reminderId == -1L) return

            val delayByMillis = intent.getLongExtra(Constants.INTENT_EXTRA_TIME_DELAY, -1L)
            if (delayByMillis != -1L) {
                Toast.makeText(context, "Delayed", Toast.LENGTH_LONG).show()
                ReminderManager.setCheckForConfirmation(context, reminderId, delayByMillis)
                NotificationManager.cancelNotification(context, reminderId)
                return
            }

            Timber.d("Reminder check run id: %d", reminderId)

            GlobalScope.launch(Dispatchers.IO) {

                val reminder = reminderRepository.getReminder(reminderId)
                val pill = pillRepository.getPillSync(reminder.pillId)

                ReminderUtil.createStandardReminderNotification(context, pill, reminder)

                // TODO Check if the reminder is confirmed, if so, don't alarm again
                if (false) {
                    ReminderManager.setCheckForConfirmation(context, reminderId)
                }
            }
        }
    }
}