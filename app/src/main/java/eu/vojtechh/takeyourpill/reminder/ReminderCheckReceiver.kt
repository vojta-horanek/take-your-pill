package eu.vojtechh.takeyourpill.reminder

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.activity.MainActivity
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

            Timber.d("Reminder check run id: %d", reminderId)

            GlobalScope.launch(Dispatchers.IO) {

                val reminder = reminderRepository.getReminder(reminderId)
                val pill = pillRepository.getPillSync(reminder.pillId)

                val notificationIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra(Constants.INTENT_EXTRA_PILL_ID, pill.id)
                }
                val pendingIntent: PendingIntent =
                    PendingIntent.getActivity(context, reminderId.toInt(), notificationIntent, 0)

                NotificationManager.createAndShowNotification(
                    context,
                    title = pill.name,
                    description = pill.getNotificationDescription(context, reminder),
                    color = pill.color.getColor(context),
                    bitmap = pill.photo,
                    pendingIntent = pendingIntent,
                    notificationId = reminder.reminderId,
                    channelId = pill.id.toString()
                )

                // TODO Check if the reminder is confirmed, if so, don't alarm again
                if (false) {
                    ReminderManager.setCheckForConfirmation(context, reminderId)
                }
            }
        }
    }
}