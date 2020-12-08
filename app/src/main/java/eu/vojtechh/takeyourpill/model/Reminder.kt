package eu.vojtechh.takeyourpill.model

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import eu.vojtechh.takeyourpill.R
import java.text.SimpleDateFormat
import java.util.*

@Entity(
    tableName = "reminder",
    foreignKeys = [ForeignKey(
        entity = BasePill::class,
        parentColumns = arrayOf("pillId"),
        childColumns = arrayOf("pillId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Reminder(
    @PrimaryKey(autoGenerate = true) val reminderId: Long = 0,
    var time: Calendar,
    var amount: Int,
    var pillId: Long
) {
    val hour
        get() = time.get(Calendar.HOUR_OF_DAY)

    val minute
        get() = time.get(Calendar.MINUTE)

    companion object {
        fun create(hour: Int = 8, minute: Int = 0, amount: Int = 1, pillId: Long): Reminder {
            val calendar = Calendar.getInstance()
            calendar.clear()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            return Reminder(time = calendar, amount = amount, pillId = pillId)
        }
    }

    val timeString: String
        get() = SimpleDateFormat("HH:mm", Locale.getDefault()).format(time.time)

    fun formattedString(context: Context) =
        context.resources.getQuantityString(R.plurals.reminder_text, amount, timeString, amount)


    // Always update list -> this must be used for instant change while editing reminders
    //      -> because we remove the item and add it, it gets passed here as oldItem and newItem
    //         and they always equal
    object DiffCallback : DiffUtil.ItemCallback<Reminder>() {
        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder) = false
        // oldItem.reminderId == newItem.reminderId

        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder) = false
        //(oldItem.time == newItem.time) && (oldItem.amount == newItem.amount)
    }
}

