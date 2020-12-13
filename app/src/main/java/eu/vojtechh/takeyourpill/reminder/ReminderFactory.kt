package eu.vojtechh.takeyourpill.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.model.Reminder
import java.util.*

object ReminderFactory {
    fun createReminder(context: Context, reminder: Reminder) {
        val alarmMgr =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, ReminderAlarmReceiver::class.java).let { intent ->
            intent.putExtra(Constants.INTENT_EXTRA_REMINDER_ID, reminder.reminderId)
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }
        val time = Calendar.getInstance()
        time.set(Calendar.HOUR_OF_DAY, reminder.hour)
        time.set(Calendar.MINUTE, reminder.minute)
        time.set(Calendar.SECOND, 0)
        time.set(Calendar.MILLISECOND, 0)
        alarmMgr.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time.timeInMillis,
            alarmIntent
        )
    }

    fun planRemindersForPill(context: Context, reminders: List<Reminder>) {
        reminders.forEach {
            val time = Calendar.getInstance()
            time.set(Calendar.HOUR_OF_DAY, it.hour)
            time.set(Calendar.MINUTE, it.minute)
            time.set(Calendar.SECOND, 0)
            time.set(Calendar.MILLISECOND, 0)
            val calendar = Calendar.getInstance()
            if (time.timeInMillis >= calendar.timeInMillis) {
                createReminder(context, it)
            }
        }
    }
}