package eu.vojtechh.takeyourpill.receiver

import android.content.Context
import android.content.Intent
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.reminder.NotificationManager

@AndroidEntryPoint
class ConfirmReceiver : HiltBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        intent.let {
            val reminderId = it.getLongExtra(Constants.INTENT_EXTRA_REMINDER_ID, -1L)
            if (reminderId == -1L) return

            Toast.makeText(context, "Confirmed", Toast.LENGTH_LONG).show()

            NotificationManager.cancelNotification(context, reminderId)

            // TODO Confirm Pill
        }
    }
}