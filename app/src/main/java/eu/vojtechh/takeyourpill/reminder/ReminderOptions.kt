package eu.vojtechh.takeyourpill.reminder

/**
 *    A class used for setting all the possible options
 *    to a pill reminder. This can be used to store
 *    the constant state of the pill (reminder)
 *    and also the current state - changing [limitDays] to
 *    stop reminding when it reaches 0...
 *
 *   Types of reminders:
 *   - infinite = [daysActive] and [daysInactive] both set to "NO" values,
 *           [repeatCount] set to [REPEAT_FOREVER]
 *           * Example: Taking pills every day for the rest of your life
 *   - finite = [daysActive] set to a value, [daysInactive] set to [NO_BREAK]
 *           [repeatCount] set to [REPEAT_FOREVER]
 *           * Example: Taking pills every day for [limitDays] days
 *   - infinite with break = [daysActive] and [daysInactive] set to a value,
 *           [repeatCount] set to [REPEAT_FOREVER]
 *           * Example: Taking pills for [limitDays] days, then taking a break for [breakDays] days
 *                    for the rest of your life
 *   - finite repeating = [daysActive], [daysInactive], [repeatCount] set to a value
 *          * Example: Taking pill for [limitDays] days, then taking a break for [breakDays] days,
 *                    this cycle repeats [repeatCount] times
 */
class ReminderOptions(
    /**
     *  Limit of days that the reminder is valid (keeps reminding)
     *  Either number of days or [NO_DAY_LIMIT]
     */
    var daysActive: Int = NO_DAY_LIMIT,
    /**
     *  Number of days between two sets of reminders lasting [daysActive]
     * Either number of days or [NO_BREAK]
     */
    var daysInactive: Int = NO_BREAK,

    var todayCycle: Int = 1

) {
    companion object {
        const val NO_DAY_LIMIT = -1
        const val NO_BREAK = -1

        fun indefinite() =
            ReminderOptions()

        fun finite(daysActive: Int) =
            ReminderOptions(daysActive = daysActive)

        fun cycling(daysActive: Int, daysInactive: Int, todayCycle: Int) =
            ReminderOptions(
                daysActive = daysActive,
                daysInactive = daysInactive,
                todayCycle = todayCycle
            )

        fun empty() = ReminderOptions()
    }

    fun isIndefinite() = daysActive == NO_DAY_LIMIT && daysInactive == NO_BREAK
    fun isFinite() = daysActive != NO_DAY_LIMIT && daysInactive == NO_BREAK
    fun isCycle() = daysActive != NO_DAY_LIMIT && daysInactive != NO_BREAK

    val displayLimit: Int
        get() = if (daysActive == NO_DAY_LIMIT) 21 else daysActive

    val displayBreak: Int
        get() = if (daysInactive == NO_BREAK) 7 else daysInactive
}