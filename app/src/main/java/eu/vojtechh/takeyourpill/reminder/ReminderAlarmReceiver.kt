package eu.vojtechh.takeyourpill.reminder

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.CallSuper
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.activity.MainActivity
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class HiltBroadcastReceiver : BroadcastReceiver() {
    @CallSuper
    override fun onReceive(context: Context, intent: Intent) {
    }
}

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

                val int = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, int, 0)


                NotificationFactory.createAndShowNotification(
                    context,
                    pill.name,
                    reminderId.toString(),
                    pill.color.getColor(context),
                    pill.photo,
                    pendingIntent,
                    reminderId
                )

                // TODO Plan next reminder for this pill, display alert
                //            ReminderFactory.createReminder(
                //                reminderId,
                //                Calendar.getInstance()
                //            ) // TODO Get next time from Database
            }
        }
    }
}