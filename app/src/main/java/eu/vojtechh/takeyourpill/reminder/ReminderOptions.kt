package eu.vojtechh.takeyourpill.reminder

data class ReminderOptions(
    var daysActive: Int = NO_DAY_LIMIT,
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
    fun isInactive(): Boolean {
        return if (isIndefinite()) false
        else todayCycle > daysActive
    }

    fun isActive() = !isInactive()

    fun nextCycleIteration() {
        todayCycle++
        if (isCycle()) {
            if (todayCycle > daysActive + daysInactive) {
                todayCycle = 1
            }
        }
    }
}