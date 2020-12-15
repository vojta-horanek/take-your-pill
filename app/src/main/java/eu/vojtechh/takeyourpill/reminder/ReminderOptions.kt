package eu.vojtechh.takeyourpill.reminder

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

) {
    companion object {
        const val NO_DAY_LIMIT = -1
        const val NO_BREAK = -1
        const val REPEAT_FOREVER = -1

        fun infinite() =
            ReminderOptions()

        fun finite(limitDays: Int) =
            ReminderOptions(limitDays = limitDays)

        fun infiniteBreak(limitDays: Int, breakDays: Int) =
            ReminderOptions(limitDays = limitDays, breakDays = breakDays)

        fun finiteRepeating(
            limitDays: Int,
            breakDays: Int,
            repeatCount: Int
        ) =
            ReminderOptions(
                limitDays = limitDays,
                breakDays = breakDays,
                repeatCount = repeatCount
            )

        fun empty() = ReminderOptions()
    }

    val displayLimit: Int
        get() = if (limitDays == NO_DAY_LIMIT) 21 else limitDays

    val displayBreak: Int
        get() = if (breakDays == NO_BREAK) 7 else breakDays

    val displayRepeat: Int
        get() = if (repeatCount == REPEAT_FOREVER) 3 else repeatCount
}