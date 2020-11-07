package eu.vojtechh.takeyourpill.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import eu.vojtechh.takeyourpill.klass.Constants
import java.util.*

object ReminderFactory {

    private lateinit var context: Context
    private lateinit var alarmMgr: AlarmManager

    fun init(context: Context) {
        if (!this::context.isInitialized) this.context = context
        if (!this::alarmMgr.isInitialized) alarmMgr =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    fun createReminder(pillId: Int, time: Calendar) {
        if (!this::context.isInitialized || !this::alarmMgr.isInitialized) return
        val alarmIntent = Intent(context, ReminderAlarmReceiver::class.java).let { intent ->
            intent.putExtra(Constants.INTENT_EXTRA_PILL_ID, pillId)
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }
        alarmMgr.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time.timeInMillis, alarmIntent)

    }
}