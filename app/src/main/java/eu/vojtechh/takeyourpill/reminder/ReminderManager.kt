package eu.vojtechh.takeyourpill.reminder

import android.app.AlarmManager
import android.content.Context
import android.os.SystemClock
import eu.vojtechh.takeyourpill.klass.Pref
import eu.vojtechh.takeyourpill.klass.getDateTimeString
import eu.vojtechh.takeyourpill.klass.getTimeString
import eu.vojtechh.takeyourpill.model.Reminder
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
                Timber.d(
                    "Next reminder is today at %s",
                    it.getMillisWithTodayDate().getDateTimeString()
                )
                createReminder(context, it)
                return@planNextReminder
            }
        }

        // no reminder for today if we get here, plan the first one for tomorrow
        val firstTomorrow = sortedByTime.firstOrNull()
        firstTomorrow?.let {
            calendar.timeInMillis = it.getMillisWithTodayDate()
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            Timber.d("Next reminder is tomorrow at %s", calendar.timeInMillis.getDateTimeString())
            createReminder(
                context,
                it.id,
                calendar.timeInMillis,
                it.calendar.timeInMillis
            )
        } ?: run {
            Timber.e("No reminder found")
        }
    }

    fun setCheckForConfirmation(
        context: Context,
        reminderId: Long,
        remindAfterMinutes: Long = Pref.remindAgainAfter.toLong()
    ) {

        val alarmIntent = ReminderUtil.getAlarmAgainIntent(context, reminderId)
        Timber.e("buttonDelay %d", Pref.buttonDelay.toLong())
        Timber.e("buttonDelay2 %d", remindAfterMinutes)
        Timber.e("remindAgainAfter %d", Pref.remindAgainAfter.toLong())

        // Trigger after [interval] minutes, then repeat every [interval] minutes
        val triggerAt = 1000 * 60 * remindAfterMinutes
        Timber.d("Setting check alarm to start in %s minutes", triggerAt.getTimeString())
        getAlarmManager(context).setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + triggerAt,
            alarmIntent
        )
    }

    private fun createReminder(
        context: Context,
        reminderId: Long,
        triggerAtMillis: Long,
        reminderTime: Long
    ) {
        Timber.d(
            "Creating a reminder for ReminderReceiver to trigger at %s",
            triggerAtMillis.getDateTimeString()
        )

        val alarmIntent = ReminderUtil.getAlarmIntent(context, reminderId, reminderTime)

        getAlarmManager(context).setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            alarmIntent
        )
    }

    private fun createReminder(context: Context, reminder: Reminder) =
        createReminder(
            context,
            reminder.id,
            reminder.getMillisWithTodayDate(),
            reminder.calendar.timeInMillis
        )

    private fun getAlarmManager(context: Context) =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}