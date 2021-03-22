package eu.vojtechh.takeyourpill.reminder

import android.app.AlarmManager
import android.content.Context
import android.os.SystemClock
import eu.vojtechh.takeyourpill.klass.Pref
import eu.vojtechh.takeyourpill.klass.getDateTimeString
import eu.vojtechh.takeyourpill.klass.getTimeString
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.Reminder
import timber.log.Timber
import java.util.*

object ReminderManager {

    // Only allow planning reminders per pill bases
    fun planNextPillReminder(context: Context, pill: Pill) {
        planNextReminder(context, pill.reminders)
    }

    /**
     * Takes [reminders] and checks which reminder should be the next to fire.
     * If it finds one it creates a reminder with [createAlarm]
     * If it does not find one it uses the first [Reminder] in a day and plans a reminder with
     * the same time but tomorrows date
     */
    private fun planNextReminder(context: Context, reminders: List<Reminder>) {
        Timber.d("Planning next reminder")

        val sortedByTime = reminders.sortedBy { it.time.timeInMillis }

        // Go trough reminders from 00:00 to 23:59 basically
        val today = Calendar.getInstance()
        sortedByTime.forEach { reminder ->
            val remindTime = reminder.getTodayMillis()
            // Only plan if the reminder time is past the current time
            if (remindTime > today.timeInMillis) {
                Timber.d("Next reminder is today at %s", remindTime.getDateTimeString())
                createAlarm(context, reminder)
                return@planNextReminder
            }
        }

        // no reminder for today if we get here, plan the first one for tomorrow
        val firstTomorrow = sortedByTime.firstOrNull()
        firstTomorrow?.let { reminder ->
            val triggerAtCalendar = Calendar.getInstance()
            triggerAtCalendar.timeInMillis = reminder.getTodayMillis()
            triggerAtCalendar.add(Calendar.DAY_OF_YEAR, 1)

            Timber.d(
                "Next reminder is tomorrow at %s",
                triggerAtCalendar.timeInMillis.getDateTimeString()
            )

            createAlarm(context, reminder.id, triggerAtCalendar.timeInMillis)
        } ?: run {
            Timber.e("No reminder found")
        }
    }

    /**
     * takes [remindAfterMillis] (defaults to the value currently stored in SharedPrefs * 1000 * 60)
     * sets a ExactAndAllowWhileIdle [AlarmManager.ELAPSED_REALTIME_WAKEUP] alarm using [AlarmManager]
     * with a pending intent from [ReminderUtil.getAlarmAgainIntent] and a trigger value of [remindAfterMillis]
     * works on reminder by reminder bases => each reminder has its own Alarm
     */
    fun createCheckAlarm(
        context: Context,
        reminderId: Long,
        remindedTime: Long,
        checkCount: Long,
        remindAfterMillis: Long = 1000 * 60 * Pref.remindAgainAfter.toLong(),
    ) {
        val alarmIntent =
            ReminderUtil.getAlarmAgainIntent(context, reminderId, remindedTime, checkCount)
        // Trigger after [interval] minutes, then repeat every [interval] minutes
        Timber.d("Setting check alarm to start in %s minutes", remindAfterMillis.getTimeString())
        getAlarmManager(context).setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + remindAfterMillis,
            alarmIntent
        )
    }

    private fun createAlarm(
        context: Context,
        reminderId: Long,
        triggerAtMillis: Long
    ) {
        Timber.d(
            "Creating a reminder for ReminderReceiver to trigger at %s",
            triggerAtMillis.getDateTimeString()
        )

        val alarmIntent = ReminderUtil.getAlarmIntent(context, reminderId)

        getAlarmManager(context).setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            alarmIntent
        )
    }

    private fun createAlarm(context: Context, reminder: Reminder) =
        createAlarm(
            context,
            reminder.id,
            reminder.getTodayMillis(),
        )

    fun getAlarmManager(context: Context) =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}