package eu.vojtechh.takeyourpill.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import eu.vojtechh.takeyourpill.klass.Constants
import java.util.*

class ReminderAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(_context: Context?, intent: Intent?) {
        val context = _context ?: return
        intent?.let {
            val pillId = it.getIntExtra(Constants.INTENT_EXTRA_PILL_ID, -1)
            if (pillId == -1) return
            // TODO Plan next reminder for this pill, display alert
            ReminderFactory.init(context)
            ReminderFactory.createReminder(
                pillId,
                Calendar.getInstance()
            ) // TODO Get next time from Database
        }
    }
}