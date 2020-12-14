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
import javax.inject.Inject

@AndroidEntryPoint
class ReminderAlarmReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var pillRepository: PillRepository

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        intent.let {
            val reminderId = it.getLongExtra(Constants.INTENT_EXTRA_REMINDER_ID, -1)
            if (reminderId == -1L) return

            GlobalScope.launch(Dispatchers.IO) {
                val pill = pillRepository.getPillByReminderId(reminderId)

                val notificationIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra(Constants.INTENT_EXTRA_PILL_ID, pill.id)
                }
                val pendingIntent: PendingIntent =
                    PendingIntent.getActivity(context, 0, notificationIntent, 0)

                NotificationManager.createAndShowNotification(
                    context,
                    title = pill.name,
                    description = pill.notificationDescription,
                    color = pill.color.getColor(context),
                    bitmap = pill.photo,
                    pendingIntent = pendingIntent,
                    notificationId = reminderId,
                    channelId = pill.id.toString()
                )

            }
        }
    }
}