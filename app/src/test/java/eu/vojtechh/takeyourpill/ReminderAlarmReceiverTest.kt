package eu.vojtechh.takeyourpill

import eu.vojtechh.takeyourpill.reminder.ReminderAlarmReceiver
import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ReminderAlarmReceiverTest {
    @Test
    fun timeFormat_isCorrect() {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)

        val time = Calendar.getInstance()
        time.set(Calendar.HOUR_OF_DAY, 8)
        time.set(Calendar.MINUTE, 0)
        time.set(Calendar.SECOND, 0)
        time.set(Calendar.MILLISECOND, 0)

        assertEquals(calendar.timeInMillis, getFormattedMillis(time.timeInMillis))
    }

    private fun getFormattedMillis(millis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        calendar.clear()
        calendar.set(Calendar.HOUR, hour)
        calendar.set(Calendar.MINUTE, minute)
        return  calendar.timeInMillis
    }

}