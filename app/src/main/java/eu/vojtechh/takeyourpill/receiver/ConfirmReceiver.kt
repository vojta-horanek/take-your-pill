package eu.vojtechh.takeyourpill.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.reminder.NotificationManager
import eu.vojtechh.takeyourpill.reminder.ReminderUtil
import eu.vojtechh.takeyourpill.repository.HistoryRepository
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ConfirmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var historyRepository: HistoryRepository

    override fun onReceive(context: Context, intent: Intent) {
        intent.let {
            val reminderId = it.getLongExtra(Constants.INTENT_EXTRA_REMINDER_ID, -1L)
            val pillId = it.getLongExtra(Constants.INTENT_EXTRA_PILL_ID, -1L)
            val remindedTime = it.getLongExtra(Constants.INTENT_EXTRA_REMINDED_TIME, -1L)

            if (reminderId == -1L || pillId == -1L || remindedTime == -1L) {
                Timber.e("Invalid number of extras passed, exiting...")
                return
            }

            var success = false
            runBlocking {

                historyRepository.getByPillIdAndTime(pillId, remindedTime)?.let { history ->
                    history.confirmed = Calendar.getInstance()
                    historyRepository.updateHistoryItem(history)
                    success = true
                } ?: run {
                    Timber.e("Couldn't find the correct history item...")
                }
                // Cancel check alarm
                ReminderUtil.getAlarmAgainIntent(context, reminderId, remindedTime, 0).cancel()

                if (success) {
                    // Hide notification
                    NotificationManager.cancelNotification(context, reminderId)
                    Toast.makeText(
                        context,
                        context.getString(R.string.confirmed),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        }
    }
}