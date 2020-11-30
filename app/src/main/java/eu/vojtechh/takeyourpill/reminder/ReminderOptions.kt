package eu.vojtechh.takeyourpill.reminder

import java.util.*

/*
    A class used for setting all the possible options
    to a pill reminder. This can be used to store
    the constant state of the pill (reminder)
    and also the current state - changing [dayLimit] to
    stop reminding when it reaches 0...

    Types of reminders:
    - infinite = [limitDays] and [breakDays] both set to "NO" values,
            [repeatCount] set to [REPEAT_FOREVER]
            * Example: Taking pills every day for the rest of your life
    - finite = [limitDays] set to a value, [breakDays] set to [NO_BREAK]
            [repeatCount] set to [REPEAT_FOREVER]
            * Example: Taking pills every day for [limitDays] days
    - infinite with break = [limitDays] and [breakDays] set to a value,
            [repeatCount] set to [REPEAT_FOREVER]
            * Example: Taking pills for [limitDays] days, then taking a break for [breakDays] days
                     for the rest of your life
    - finite repeating = [limitDays], [breakDays], [repeatCount] set to a value
            * Example: Taking pill for [limitDays] days, then taking a break for [breakDays] days,
                     this cycle repeats [repeatCount] times
 */
class ReminderOptions(
    /*
        Limit of days that the reminder is valid (keeps reminding)
        Either number of days or [NO_DAY_LIMIT]
     */
    var limitDays: Int = NO_DAY_LIMIT,
    /*
        Number of days between two sets of reminders lasting [limitDays]
        Either number of days or [NO_BREAK]
    */
    var breakDays: Int = NO_BREAK,
    /*
        Number of repeats for a finite repeating reminder
        Either a number or [REPEAT_FOREVER]
     */
    var repeatCount: Int = REPEAT_FOREVER,

    /* List of times that the reminder will fire each day */
    var remindTimes: MutableList<Calendar>
) {
    companion object {
        const val NO_DAY_LIMIT = -1
        const val NO_BREAK = -1
        const val REPEAT_FOREVER = -1

        fun Infinite(remindTimes: MutableList<Calendar>) =
            ReminderOptions(remindTimes = remindTimes)

        fun Finite(remindTimes: MutableList<Calendar>, limitDays: Int) =
            ReminderOptions(remindTimes = remindTimes, limitDays = limitDays)

        fun InfiniteBreak(remindTimes: MutableList<Calendar>, limitDays: Int, breakDays: Int) =
            ReminderOptions(remindTimes = remindTimes, limitDays = limitDays, breakDays = breakDays)

        fun FiniteRepeating(
            remindTimes: MutableList<Calendar>,
            limitDays: Int,
            breakDays: Int,
            repeatCount: Int
        ) =
            ReminderOptions(
                remindTimes = remindTimes,
                limitDays = limitDays,
                breakDays = breakDays,
                repeatCount = repeatCount
            )

        fun Empty() = ReminderOptions(remindTimes = mutableListOf())
    }

    // TODO Very much not finished and broken
    val nextReminder: Calendar?
        get() {
            val nextReminder = remindTimes.firstOrNull() ?: return null
            val today = Calendar.getInstance()

            // Infinite reminder or finite reminder that is still alive
            if (limitDays == NO_DAY_LIMIT || limitDays != 0) {
                nextReminder.set(
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DATE)
                )
                return nextReminder

            }
            // A reminder that is dead but only has a break
            else if (limitDays == 0 && breakDays != NO_BREAK) {
                nextReminder.set(
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DATE)
                )
                // add the break day count to the calendar
                nextReminder.add(Calendar.DATE, breakDays)
                return nextReminder
            }
            // Reminder that is both dead and does not have a break => completely dead
            else if (limitDays == 0 && breakDays == NO_BREAK) {
                return null
            }
            // FIXME Just a placeholder for the compiler
            return null
        }
}