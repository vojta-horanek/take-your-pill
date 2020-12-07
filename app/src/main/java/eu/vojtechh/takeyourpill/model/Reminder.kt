package eu.vojtechh.takeyourpill.model

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import eu.vojtechh.takeyourpill.R
import java.text.SimpleDateFormat
import java.util.*

data class Reminder(
    val time: Calendar,
    var amount: Int
) {

    val hour = time.get(Calendar.HOUR_OF_DAY)
    val minute = time.get(Calendar.MINUTE)

    companion object {
        fun create(hour: Int = 8, minute: Int = 0, amount: Int = 1): Reminder {
            val calendar = Calendar.getInstance()
            calendar.clear()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            return Reminder(calendar, amount)
        }
    }

    val timeString: String
        get() = SimpleDateFormat("HH:mm", Locale.getDefault()).format(time.time)

    fun formattedString(context: Context) =
        context.resources.getQuantityString(R.plurals.reminder_text, amount, timeString, amount)


    object DiffCallback : DiffUtil.ItemCallback<Reminder>() {
        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder) =
            oldItem.time == newItem.time

        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder) =
            (oldItem.time == newItem.time) && (oldItem.amount == newItem.amount)
    }
}

