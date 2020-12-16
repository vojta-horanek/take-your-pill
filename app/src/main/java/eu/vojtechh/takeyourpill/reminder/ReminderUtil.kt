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
import timber.log.Timber

object ReminderUtil {
    fun getNotificationPendingIntent(context: Context, pillId: Long): PendingIntent {
        val args = Bundle()
        args.putLong(Constants.INTENT_EXTRA_PILL_ID, pillId)
        return NavDeepLinkBuilder(context)
            .setGraph(R.navigation.navigation_graph)
            .setDestination(R.id.details)
            .setArguments(args)
            .createPendingIntent()
    }

    fun getConfirmPendingIntent(context: Context, reminderId: Long): PendingIntent =
        Intent(context, ConfirmReceiver::class.java).let { intent ->
            intent.putExtra(Constants.INTENT_EXTRA_REMINDER_ID, reminderId)
            PendingIntent.getBroadcast(context, reminderId.toInt(), intent, 0)
        }

    fun getDelayPendingIntent(
        context: Context,
        reminderId: Long,
        delayByMillis: Long
    ): PendingIntent =
        Intent(context, CheckReceiver::class.java).let { intent ->
            intent.putExtra(Constants.INTENT_EXTRA_REMINDER_ID, reminderId)
            intent.putExtra(Constants.INTENT_EXTRA_TIME_DELAY, delayByMillis)
            PendingIntent.getBroadcast(context, reminderId.toInt(), intent, 0)
        }


    fun createStandardReminderNotification(context: Context, pill: Pill, reminder: Reminder) {
        Timber.d("Creating standard notification")
        NotificationManager.createAndShowNotification(
            context,
            title = pill.name,
            description = pill.getNotificationDescription(context, reminder),
            color = pill.color.getColor(context),
            bitmap = pill.photo,
            pendingIntent = getNotificationPendingIntent(context, pill.id),
            confirmPendingIntent = getConfirmPendingIntent(
                context,
                reminder.reminderId
            ),
            delayPendingIntent = getDelayPendingIntent(
                context,
                reminder.reminderId,
                1000 * 60 * Pref.buttonDelay.toLong()
            ),
            // TODO Set the actual fullscreen intent
            fullscreenPendingIntent = getNotificationPendingIntent(context, pill.id),
            notificationId = reminder.reminderId,
            channelId = pill.id.toString(),
            whenMillis = reminder.getMillisWithTodayDate()
        )
    }
}