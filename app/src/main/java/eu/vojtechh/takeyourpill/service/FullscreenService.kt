package eu.vojtechh.takeyourpill.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.reminder.ReminderUtil
import eu.vojtechh.takeyourpill.repository.PillRepository
import eu.vojtechh.takeyourpill.repository.ReminderRepository
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class FullscreenService : Service() {

    @Inject
    lateinit var reminderRepository: ReminderRepository

    @Inject
    lateinit var pillRepository: PillRepository

    companion object {
        fun startService(context: Context, reminderId: Long) {
            val startIntent = Intent(context, FullscreenService::class.java)
            startIntent.putExtra(Constants.INTENT_EXTRA_REMINDER_ID, reminderId)
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, FullscreenService::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent ?: run {
            stopSelf()
            return START_NOT_STICKY
        }

        val reminderId = intent.getLongExtra(Constants.INTENT_EXTRA_REMINDER_ID, -1L)
        if (reminderId == -1L) {
            stopSelf()
            return START_NOT_STICKY
        }

        runBlocking {
            val reminder = reminderRepository.getReminder(reminderId)
            val pill = pillRepository.getPill(reminder.pillId)
            val notification =
                ReminderUtil.getFullscreenNotification(this@FullscreenService, pill, reminder)
            startForeground(reminderId.toInt(), notification)
        }

        return START_NOT_STICKY

    }
}