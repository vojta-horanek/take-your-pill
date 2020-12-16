package eu.vojtechh.takeyourpill.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.klass.Pref
import eu.vojtechh.takeyourpill.model.Reminder
import eu.vojtechh.takeyourpill.receiver.CheckReceiver
import eu.vojtechh.takeyourpill.receiver.ReminderReceiver
import timber.log.Timber
import java.util.*

object ReminderManager {

    fun planNextReminder(context: Context, reminders: List<Reminder>) {
        val sortedByTime = reminders.sortedBy { rem -> rem.calendar.time }
        val calendar = Calendar.getInstance()

        Timber.d("Planning next reminder")

        sortedByTime.forEach {
            // Only plan if the reminder time is past the current time
            if (it.getMillisWithTodayDate() > calendar.timeInMillis) {
                createReminder(context, it)
                return@planNextReminder
            }
        }

        Timber.d("Next reminder is tomorrow")
        // no reminder for today if we get here, plan the first one for tomorrow
        val firstTomorrow = sortedByTime[0]
        calendar.timeInMillis = firstTomorrow.getMillisWithTodayDate()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        createReminder(
            context,
            firstTomorrow.reminderId,
            calendar.timeInMillis,
            firstTomorrow.calendar.timeInMillis
        )
    }

    fun setCheckForConfirmation(
        context: Context,
        reminderId: Long,
        interval: Long = Pref.remindAgainAfter.toLong()
    ) {
        val alarmMgr =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, CheckReceiver::class.java).let { intent ->
            intent.putExtra(Constants.INTENT_EXTRA_REMINDER_ID, reminderId)
            PendingIntent.getBroadcast(
                context,
                reminderId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        // Trigger after [interval] minutes, then repeat every [interval] minutes
        val triggerAt = SystemClock.elapsedRealtime() + 1000 * 60 * interval
        Timber.d("Setting check alarm at %d", triggerAt)
        alarmMgr.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerAt,
            alarmIntent
        )
    }

    private fun createReminder(
        context: Context,
        id: Long,
        triggerMillis: Long,
        reminderTime: Long
    ) {
        Timber.d("Creating a reminder for AlarmReceiver at %d", triggerMillis)
        val alarmMgr =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, ReminderReceiver::class.java).let { intent ->
            intent.putExtra(Constants.INTENT_EXTRA_REMINDER_TIME, reminderTime)
            PendingIntent.getBroadcast(context, id.toInt(), intent, 0)
        }
        alarmMgr.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerMillis,
            alarmIntent
        )
    }

    private fun createReminder(context: Context, reminder: Reminder) =
        createReminder(
            context,
            reminder.reminderId,
            reminder.getMillisWithTodayDate(),
            reminder.calendar.timeInMillis
        )
}