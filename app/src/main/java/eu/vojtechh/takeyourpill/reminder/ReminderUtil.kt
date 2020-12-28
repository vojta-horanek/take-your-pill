package eu.vojtechh.takeyourpill.reminder

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavDeepLinkBuilder
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.klass.Pref
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.Reminder
import eu.vojtechh.takeyourpill.receiver.CheckReceiver
import eu.vojtechh.takeyourpill.receiver.ConfirmReceiver
import eu.vojtechh.takeyourpill.receiver.ReminderReceiver
import timber.log.Timber

object ReminderUtil {
    private fun getNotificationClickIntent(context: Context, pillId: Long): PendingIntent {
        val args = Bundle()
        args.putLong(Constants.INTENT_EXTRA_PILL_ID, pillId)
        return NavDeepLinkBuilder(context)
            .setGraph(R.navigation.navigation_graph)
            .setDestination(R.id.details)
            .setArguments(args)
            .createPendingIntent()
    }

    private fun getNotificationConfirmIntent(
        context: Context,
        reminderId: Long,
        pillId: Long,
        remindedTime: Long
    ): PendingIntent =
        Intent(context, ConfirmReceiver::class.java).let { intent ->
            intent.putExtra(Constants.INTENT_EXTRA_REMINDER_ID, reminderId)
            intent.putExtra(Constants.INTENT_EXTRA_PILL_ID, pillId)
            intent.putExtra(Constants.INTENT_EXTRA_REMINDED_TIME, remindedTime)
            PendingIntent.getBroadcast(
                context,
                reminderId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

    private fun getNotificationDelayIntent(
        context: Context,
        reminderId: Long,
        delayByMillis: Long
    ): PendingIntent =
        Intent(context, CheckReceiver::class.java).let { intent ->
            intent.putExtra(Constants.INTENT_EXTRA_REMINDER_ID, reminderId)
            intent.putExtra(Constants.INTENT_EXTRA_TIME_DELAY, delayByMillis)
            PendingIntent.getBroadcast(
                context,
                reminderId.toInt(),
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        }

    fun getAlarmAgainIntent(
        context: Context,
        reminderId: Long,
        remindedTime: Long,
    ): PendingIntent = Intent(context, CheckReceiver::class.java).let { intent ->
        intent.putExtra(Constants.INTENT_EXTRA_REMINDER_ID, reminderId)
        intent.putExtra(Constants.INTENT_EXTRA_REMINDED_TIME, remindedTime)
        PendingIntent.getBroadcast(context, reminderId.toInt(), intent, PendingIntent.FLAG_ONE_SHOT)
    }

    fun getAlarmIntent(
        context: Context,
        reminderId: Long,
        reminderTime: Long
    ): PendingIntent =
        Intent(context, ReminderReceiver::class.java).let { intent ->
            intent.putExtra(Constants.INTENT_EXTRA_REMINDER_TIME, reminderTime)
            // TODO Test if this is working correctly
            PendingIntent.getBroadcast(
                context,
                reminderId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

    fun createReminderNotification(context: Context, pill: Pill, reminder: Reminder) {
        Timber.d("Creating reminder notification")
        NotificationManager.createAndShowNotification(
            context,
            title = pill.name,
            description = pill.getNotificationDescription(context, reminder),
            color = pill.color.getColor(context),
            bitmap = pill.photo,
            pendingIntent = getNotificationClickIntent(context, pill.id),
            confirmPendingIntent = getNotificationConfirmIntent(
                context,
                reminder.id,
                pill.id,
                reminder.getMillisWithTodayDate()
            ),
            delayPendingIntent = getNotificationDelayIntent(
                context,
                reminder.id,
                Pref.buttonDelay.toLong() * 1000 * 60
            ),
            notificationId = reminder.id,
            channelId = pill.id.toString(),
            whenMillis = reminder.getMillisWithTodayDate()
        )
    }
}