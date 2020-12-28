package eu.vojtechh.takeyourpill.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.klass.Pref
import eu.vojtechh.takeyourpill.klass.getTimeString
import eu.vojtechh.takeyourpill.reminder.NotificationManager
import eu.vojtechh.takeyourpill.reminder.ReminderManager
import eu.vojtechh.takeyourpill.reminder.ReminderUtil
import eu.vojtechh.takeyourpill.repository.HistoryRepository
import eu.vojtechh.takeyourpill.repository.PillRepository
import eu.vojtechh.takeyourpill.repository.ReminderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CheckReceiver : BroadcastReceiver() {

    @Inject
    lateinit var pillRepository: PillRepository

    @Inject
    lateinit var reminderRepository: ReminderRepository

    @Inject
    lateinit var historyRepository: HistoryRepository

    override fun onReceive(context: Context, intent: Intent) {
        intent.let {

            val reminderId = intent.getLongExtra(Constants.INTENT_EXTRA_REMINDER_ID, -1L)
            if (reminderId == -1L) return

            val remindedTime = intent.getLongExtra(Constants.INTENT_EXTRA_REMINDED_TIME, -1L)
            if (remindedTime == -1L) return

            val delayByMillis = intent.getLongExtra(Constants.INTENT_EXTRA_TIME_DELAY, -1L)
            if (delayByMillis != -1L) {
                ReminderManager.setCheckForConfirmation(
                    context,
                    reminderId,
                    delayByMillis,
                    remindedTime
                )
                Timber.d("Set check alarm to start in %s minutes", delayByMillis.getTimeString())
                NotificationManager.cancelNotification(context, reminderId)
                return
            }


            Timber.d(
                "Received reminder id: %d, remindedTime: %d",
                reminderId,
                remindedTime
            )

            GlobalScope.launch(Dispatchers.IO) {

                val reminder = reminderRepository.getReminder(reminderId)
                val pill = pillRepository.getPillSync(reminder.pillId)

                ReminderUtil.createReminderNotification(context, pill, reminder)

                val history = historyRepository.getByPillIdAndTime(pill.id, remindedTime)
                if (Pref.remindAgain && !history.hasBeenConfirmed) {
                    ReminderManager.setCheckForConfirmation(context, reminderId, remindedTime)
                }
            }
        }
    }
}