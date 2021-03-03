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

    /**
     * Takes [reminders] and checks which reminder should be the next to fire.
     * If it finds one it creates a reminder with [createReminder]
     * If it does not find one it uses the first [Reminder] in a day and plans a reminder with
     * the same time but tomorrows date
     */
    private fun planNextReminder(context: Context, reminders: List<Reminder>) {
        val sortedByTime = reminders.sortedBy { rem -> rem.time.time }
        val calendar = Calendar.getInstance()

        Timber.d("Planning next reminder")

        // Go trough reminders from 00:00 to 23:59 basically
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
            createReminder(context, it.id, calendar.timeInMillis, it.time.timeInMillis)
        } ?: run {
            Timber.e("No reminder found")
        }
    }

    fun planNextPillReminder(context: Context, pill: Pill): Pill {
        val today = Calendar.getInstance()
        with(pill.options) {
            // if this is this pill first reminder today
            pill.lastReminderDate?.let {
                if (it.get(Calendar.DAY_OF_YEAR) !=
                    today.get(Calendar.DAY_OF_YEAR)
                ) {
                    nextCycleIteration()
                    pill.lastReminderDate = today
                }
            } ?: run {
                pill.lastReminderDate = today
            }

            planNextReminder(context, pill.reminders)
        }
        return pill
    }

    /**
     * takes [remindAfterMillis] (defaults to the value currently stored in SharedPrefs * 1000 * 60)
     * sets a ExactAndAllowWhileIdle [AlarmManager.ELAPSED_REALTIME_WAKEUP] alarm using [AlarmManager]
     * with a pending intent from [ReminderUtil.getAlarmAgainIntent] and a trigger value of [remindAfterMillis]
     * works on reminder by reminder bases => each reminder has its own Alarm
     */
    fun setCheckForConfirmation(
        context: Context,
        reminderId: Long,
        remindedTime: Long,
        remindAfterMillis: Long = 1000 * 60 * Pref.remindAgainAfter.toLong(),
    ) {
        val alarmIntent = ReminderUtil.getAlarmAgainIntent(context, reminderId, remindedTime)
        // Trigger after [interval] minutes, then repeat every [interval] minutes
        Timber.d("Setting check alarm to start in %s minutes", remindAfterMillis.getTimeString())
        getAlarmManager(context).setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + remindAfterMillis,
            alarmIntent
        )
    }

    /**
     * takes [reminderTime] that a reminder or multiple reminders should be triggered with
     * [reminderId] only represents a single reminder and is only used to fire the PendingIntent for
     * [eu.vojtechh.takeyourpill.receiver.ReminderReceiver]
     * sets a ExactAndAllowWhileIdle [AlarmManager.RTC_WAKEUP] alarm using [AlarmManager]
     * with a pending intent from [ReminderUtil.getAlarmIntent] and a trigger value of [triggerAtMillis]
     * works on a time bases, more reminders can have the same fire time, this function should be able to handle that
     */
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
            reminder.time.timeInMillis
        )

    private fun getAlarmManager(context: Context) =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}