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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
            if (reminderId == -1L) return
            if (pillId == -1L) return
            if (remindedTime == -1L) return

            GlobalScope.launch(Dispatchers.IO) {

                val history = historyRepository.getByPillIdAndTime(pillId, remindedTime)

                history.historyEntity.confirmed = Calendar.getInstance()

                historyRepository.updateHistoryItem(history.historyEntity)

                ReminderUtil.getAlarmAgainIntent(context, reminderId, remindedTime).cancel()
            }
            NotificationManager.cancelNotification(context, reminderId)
            Toast.makeText(context, context.getString(R.string.confirmed), Toast.LENGTH_SHORT)
                .show()
        }
    }
}