package eu.vojtechh.takeyourpill.reminder

import java.util.*

data class ReminderOptions(
    var daysActive: Int = NO_DAY_LIMIT,
    var daysInactive: Int = NO_BREAK,
    var todayCycle: Int = 1,
    var lastReminderDate: Calendar? = null
) {
    companion object {
        private const val NO_DAY_LIMIT = -1
        private const val NO_BREAK = -1

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

    fun isActive() = when {
        isIndefinite() -> true
        else -> todayCycle <= daysActive
    }

    fun isInactive() = !isActive()

    fun nextCycleIteration() {
        todayCycle++
        if (isCycle()) {
            if (todayCycle > daysActive + daysInactive) {
                todayCycle = 1
            }
        }
    }
}