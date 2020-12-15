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
import javax.inject.Inject

@AndroidEntryPoint
class ReminderAlarmReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var pillRepository: PillRepository

    @Inject
    lateinit var reminderRepository: ReminderRepository

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        intent.let {
            val reminderTime = it.getLongExtra(Constants.INTENT_EXTRA_REMINDER_TIME, -1L)
            if (reminderTime == -1L) return

            GlobalScope.launch(Dispatchers.IO) {
                val reminders = reminderRepository.getRemindersBasedOnTime(reminderTime)

                for (reminder in reminders) {
                    val pill = pillRepository.getPillSync(reminder.pillId)

                    val notificationIntent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        putExtra(Constants.INTENT_EXTRA_PILL_ID, pill.id)
                    }
                    val pendingIntent: PendingIntent =
                        PendingIntent.getActivity(context, 0, notificationIntent, 0)

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
                    ReminderManager.setCheckForConfirmation(context, reminder.reminderId)
                }

                ReminderManager.planNextReminder(context, reminderRepository.getAllReminders())
            }
        }
    }
}