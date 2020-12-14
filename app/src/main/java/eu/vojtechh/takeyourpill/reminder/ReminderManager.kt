package eu.vojtechh.takeyourpill.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.model.Reminder
import java.util.*

object ReminderManager {
    fun createReminder(context: Context, reminder: Reminder) {
        val alarmMgr =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, ReminderAlarmReceiver::class.java).let { intent ->
            intent.putExtra(Constants.INTENT_EXTRA_REMINDER_ID, reminder.reminderId)
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }
        alarmMgr.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminder.getMillisWithTodayDate(),
            alarmIntent
        )
    }

    fun planReminders(context: Context, reminders: List<Reminder>) {
        reminders.forEach {
            val calendar = Calendar.getInstance()
            if (it.getMillisWithTodayDate() >= calendar.timeInMillis) {
                createReminder(context, it)
            }
        }
    }
}