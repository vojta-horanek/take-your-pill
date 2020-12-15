package eu.vojtechh.takeyourpill.reminder

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.activity.MainActivity
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.klass.HiltBroadcastReceiver
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ReminderAlarmReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var pillRepository: PillRepository

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        intent.let {
            val reminderTime = it.getLongExtra(Constants.INTENT_EXTRA_REMINDER_TIME, -1L)
            if (reminderTime == -1L) return

            val reminders = pillRepository.getRemindersBasedOnTime(getFormattedMillis(reminderTime))

            GlobalScope.launch(Dispatchers.IO) {
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

                    ReminderManager.planNextReminder(context, pillRepository.getAllReminders())
                }
            }
        }
    }

    // Sets the calendar to have the same value as in the reminders table
    // -> stripping of the year, month, day
    private fun getFormattedMillis(millis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        calendar.clear()
        calendar.set(Calendar.HOUR, hour)
        calendar.set(Calendar.MINUTE, minute)
        return  calendar.timeInMillis
    }
}