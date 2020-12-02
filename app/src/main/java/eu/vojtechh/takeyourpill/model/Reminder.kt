package eu.vojtechh.takeyourpill.model

import androidx.recyclerview.widget.DiffUtil
import java.text.SimpleDateFormat
import java.util.*

data class Reminder(
    val time: Calendar,
    val amount: Int
) {

    val timeString: String
        get() = SimpleDateFormat("HH:mm", Locale.getDefault()).format(time.time)

    object DiffCallback : DiffUtil.ItemCallback<Reminder>() {
        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder) =
            oldItem.time == newItem.time

        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder) =
            (oldItem.time == newItem.time) && (oldItem.amount == newItem.amount)
    }
}

