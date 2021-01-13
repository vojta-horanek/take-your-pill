package eu.vojtechh.takeyourpill.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.reminder.ReminderManager
import eu.vojtechh.takeyourpill.repository.ReminderRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reminderRepository: ReminderRepository

    override fun onReceive(context: Context, intent: Intent) {
        GlobalScope.launch {
            if (intent.action == "android.intent.action.BOOT_COMPLETED") {
                val reminders = reminderRepository.getAllReminders()
                ReminderManager.planNextReminder(context, reminders)
            }
        }
    }

}