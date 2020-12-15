package eu.vojtechh.takeyourpill.klass

import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.reminder.ReminderManager
import eu.vojtechh.takeyourpill.repository.ReminderRepository
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var reminderRepository: ReminderRepository

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val reminders = reminderRepository.getAllReminders()
            ReminderManager.planNextReminder(context, reminders)
        }
    }

}