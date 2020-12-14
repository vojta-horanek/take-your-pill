package eu.vojtechh.takeyourpill.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import eu.vojtechh.takeyourpill.R

object NotificationManager {

    fun createAndShowNotification(
        context: Context,
        title: String,
        description: String,
        color: Int,
        bitmap: Bitmap?,
        pendingIntent: PendingIntent,
        notificationId: Long,
        channelId: String
    ) {

        // create notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_pill)
            .setContentTitle(title)
            .setContentText(description)
            .setColor(color)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // set notification style to BigPicture if the pill has aq photo
        bitmap?.let {
            builder.setLargeIcon(bitmap)
            builder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(it)
                    .bigLargeIcon(null)
            )
        } ?: run {
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(description))
        }

        // show notification to the user
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId.toInt(), builder.build())
        }

    }

    fun createNotificationChannel(context: Context, id: String, name: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance)
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun removeNotificationChannel(context: Context, id: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.deleteNotificationChannel(id)
        }
    }

}