package eu.vojtechh.takeyourpill.reminder

import android.app.AlarmManager
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
class ReminderCheckReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var pillRepository: PillRepository

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        intent.let {

            val reminderId = intent.getLongExtra(Constants.INTENT_EXTRA_PILL_ID, -1L)
            if (reminderId == -1L) return

            GlobalScope.launch(Dispatchers.IO) {

                // TODO Check if the reminder is confirmed, if so, cancel alarm
                if (true) {
                    val alarmMgr =
                        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val alarmIntent =
                        Intent(context, ReminderCheckReceiver::class.java).let { intent ->
                            intent.putExtra(Constants.INTENT_EXTRA_REMINDER_ID, reminderId)
                            PendingIntent.getBroadcast(context, reminderId.toInt(), intent, 0)
                        }
                    alarmMgr.cancel(alarmIntent)
                }

                val reminder = pillRepository.getReminder(reminderId)

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
            }
        }
    }
}