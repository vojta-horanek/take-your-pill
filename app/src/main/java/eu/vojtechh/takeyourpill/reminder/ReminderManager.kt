package eu.vojtechh.takeyourpill.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.model.Reminder
import java.util.*

object ReminderManager {

    fun planNextReminder(context: Context, reminders: List<Reminder>) {
        val sortedByTime = reminders.sortedBy { rem -> rem.calendar.time }
        val calendar = Calendar.getInstance()

        // TODO Consider next day
        sortedByTime.forEach {
            // Only plan if the reminder time is past the current time
            if (it.getMillisWithTodayDate() > calendar.timeInMillis) {
                createReminder(context, it)
                return@planNextReminder
            }
        }
        // no reminder for today if we get here, plan the first one for tomorrow
        val firstTomorrow = sortedByTime[0]
        calendar.timeInMillis = firstTomorrow.getMillisWithTodayDate()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        createReminder(context, firstTomorrow.reminderId, calendar.timeInMillis)
    }

    private fun createReminder(context: Context, id: Long, millis: Long) {
        val alarmMgr =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, ReminderAlarmReceiver::class.java).let { intent ->
            intent.putExtra(Constants.INTENT_EXTRA_REMINDER_ID, id)
            PendingIntent.getBroadcast(context, id.toInt(), intent, 0)
        }
        alarmMgr.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            millis,
            alarmIntent
        )
    }

    private fun createReminder(context: Context, reminder: Reminder) =
        createReminder(context, reminder.reminderId, reminder.getMillisWithTodayDate())

}