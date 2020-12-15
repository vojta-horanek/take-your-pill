package eu.vojtechh.takeyourpill.reminder

import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import androidx.navigation.NavDeepLinkBuilder
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.klass.Constants

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
}