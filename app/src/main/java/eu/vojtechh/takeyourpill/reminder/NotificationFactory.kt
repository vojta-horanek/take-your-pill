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
import eu.vojtechh.takeyourpill.klass.Constants

object NotificationFactory {

    // TODO Create channel ID for each pill
    fun createAndShowNotification(
        context: Context,
        title: String,
        description: String,
        color: Int,
        bitmap: Bitmap?,
        pendingIntent: PendingIntent,
        id: Long
    ) {

        createNotificationChannel(context)
        val builder = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_pill)
            .setContentTitle(title)
            .setContentText(description)
            .setColor(color)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

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

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(id.toInt(), builder.build())
        }

    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = Constants.CHANNEL_ID
            val descriptionText = Constants.CHANNEL_ID
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}