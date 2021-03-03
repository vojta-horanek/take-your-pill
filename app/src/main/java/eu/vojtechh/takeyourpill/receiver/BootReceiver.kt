package eu.vojtechh.takeyourpill.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.reminder.ReminderManager
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var pillRepository: PillRepository

    override fun onReceive(context: Context, intent: Intent) {
        GlobalScope.launch {
            if (intent.action == "android.intent.action.BOOT_COMPLETED") {
                val pills = pillRepository.getAllPillsSync()
                pills.forEach {
                    ReminderManager.planNextPillReminder(context, it)
                }
            }
        }
    }
}