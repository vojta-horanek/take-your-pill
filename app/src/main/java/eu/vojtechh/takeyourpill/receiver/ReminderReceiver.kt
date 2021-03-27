package eu.vojtechh.takeyourpill.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.klass.DayOfYear
import eu.vojtechh.takeyourpill.klass.Pref
import eu.vojtechh.takeyourpill.klass.goAsync
import eu.vojtechh.takeyourpill.model.History
import eu.vojtechh.takeyourpill.reminder.ReminderManager
import eu.vojtechh.takeyourpill.reminder.ReminderUtil
import eu.vojtechh.takeyourpill.repository.HistoryRepository
import eu.vojtechh.takeyourpill.repository.PillRepository
import eu.vojtechh.takeyourpill.repository.ReminderRepository
import eu.vojtechh.takeyourpill.service.FullscreenService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var pillRepository: PillRepository

    @Inject
    lateinit var reminderRepository: ReminderRepository

    @Inject
    lateinit var historyRepository: HistoryRepository

    override fun onReceive(context: Context, intent: Intent) {

        val reminderId = intent.getLongExtra(Constants.INTENT_EXTRA_REMINDER_ID, -1L)
        if (reminderId == -1L) {
            Timber.e("Haven't received a reminder ID, exiting...")
            return
        }

        Timber.d("Received reminder ID: %s", reminderId)

        goAsync(GlobalScope, Dispatchers.IO) {

            val reminder = reminderRepository.getReminder(reminderId)
            val pill = pillRepository.getPill(reminder.pillId)

            Timber.d("Pill before change: %s", pill.options.toString())
            Timber.d("Pill before change: %s", pill.lastReminderDate.toString())

            val today = Calendar.getInstance()
            // If last reminder date is null, then this is the first reminder
            pill.lastReminderDate?.let { lastDate ->
                // Only add next cycle if this is the first reminder today
                if (lastDate.DayOfYear != today.DayOfYear) {
                    pill.options.nextCycleIteration()
                    pill.lastReminderDate = today
                }
            } ?: run {
                pill.lastReminderDate = today
            }

            pillRepository.updatePill(pill)

            Timber.d("Pill after change: %s", pill.options.toString())
            Timber.d("Pill after change: %s", pill.lastReminderDate.toString())

            // If pill is active, create a notification, insert history and schedule a check
            if (pill.options.isActive()) {

                Timber.d("This pill is active, lets remind...")

                val todayReminderCalendar = reminder.getTodayCalendar()

                if (Pref.alertStyle) {
                    FullscreenService.startService(context, reminderId)
                } else {
                    ReminderUtil.createReminderNotification(context, pill, reminder)
                }
                val history = History(
                    pillId = pill.id,
                    reminded = todayReminderCalendar,
                    amount = reminder.amount
                )
                historyRepository.insertHistoryItem(history)

                // Schedule a check reminder if enabled and fullscreen intent is disabled
                if (Pref.remindAgain && !Pref.alertStyle) {
                    ReminderManager.createCheckAlarm(
                        context,
                        reminder.id,
                        todayReminderCalendar.timeInMillis,
                        0
                    )
                }

            } else { // If pill is not active, check if it is finite
                if (pill.options.isFinite()) {
                    Timber.d("This pill is finite and inactive -> not planing any reminder")
                    // If the pill is finite and inactive, it means we will not be reminding it anymore
                    // we can exit so we do not plan the next reminder
                    return@goAsync
                }
            }

            // Plan next reminder
            ReminderManager.planNextPillReminder(context, pill)
        }
    }
}