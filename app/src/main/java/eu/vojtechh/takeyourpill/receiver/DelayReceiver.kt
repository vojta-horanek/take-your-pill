package eu.vojtechh.takeyourpill.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.klass.Pref
import eu.vojtechh.takeyourpill.klass.getTimeString
import eu.vojtechh.takeyourpill.reminder.NotificationManager
import eu.vojtechh.takeyourpill.reminder.ReminderManager
import eu.vojtechh.takeyourpill.reminder.ReminderUtil
import eu.vojtechh.takeyourpill.service.FullscreenService
import timber.log.Timber

@AndroidEntryPoint
class DelayReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(Constants.INTENT_EXTRA_REMINDER_ID, -1L)
        val remindedTime = intent.getLongExtra(Constants.INTENT_EXTRA_REMINDED_TIME, -1L)
        val delayByMillis = intent.getLongExtra(Constants.INTENT_EXTRA_TIME_DELAY, -1L)

        if (delayByMillis == -1L || remindedTime == -1L || reminderId == -1L) {
            Timber.e("Invalid number of extras passed, exiting...")
            return
        }

        if (Pref.alertStyle) {
            FullscreenService.stopService(context)
        } else {
            // Cancel check reminder
            val again = ReminderUtil.getAlarmAgainIntent(context, reminderId, remindedTime, 0)
            again.cancel()
            ReminderManager.getAlarmManager(context).cancel(again)
        }

        ReminderManager.createCheckAlarm(
            context,
            reminderId,
            remindedTime,
            0, // Reset check counter
            delayByMillis
        )
        Timber.d(
            "Check alarm has been set to start in %s minutes",
            delayByMillis.getTimeString()
        )
        NotificationManager.cancelNotification(context, reminderId)
    }
}