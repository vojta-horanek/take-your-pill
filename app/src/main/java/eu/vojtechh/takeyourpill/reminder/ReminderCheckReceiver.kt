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

                // TODO Check if the reminder is confirmed, if so, don't alarm again
                if (false) {
                    ReminderManager.setCheckForConfirmation(context, reminderId)
                }
            }
        }
    }
}