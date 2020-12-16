package eu.vojtechh.takeyourpill.reminder

import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
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

                    NotificationManager.createAndShowNotification(
                        context,
                        title = pill.name,
                        description = pill.getNotificationDescription(context, reminder),
                        color = pill.color.getColor(context),
                        bitmap = pill.photo,
                        pendingIntent = ReminderUtil.getNotificationPendingIntent(context, pill.id),
                        notificationId = reminder.reminderId,
                        channelId = pill.id.toString(),
                        whenMillis = reminder.getMillisWithTodayDate()
                    )
                    ReminderManager.setCheckForConfirmation(context, reminder.reminderId)
                }

                ReminderManager.planNextReminder(context, reminderRepository.getAllReminders())
            }
        }
    }
}