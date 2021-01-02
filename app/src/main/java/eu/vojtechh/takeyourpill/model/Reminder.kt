package eu.vojtechh.takeyourpill.model

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.klass.hour
import eu.vojtechh.takeyourpill.klass.minute
import java.text.DateFormat
import java.util.*

@Entity(
    tableName = "reminder",
    foreignKeys = [ForeignKey(
        entity = PillEntity::class,
        parentColumns = arrayOf("pillId"),
        childColumns = arrayOf("pillId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "reminderId")
    val id: Long = 0,

    var calendar: Calendar,

    var amount: Int,

    @ColumnInfo(index = true)
    var pillId: Long
) {
    val hour
        get() = calendar.hour

    val minute
        get() = calendar.minute

    fun getMillisWithTodayDate() = getCalendarWithTodayDate().timeInMillis

    fun getCalendarWithTodayDate(): Calendar {
        val time = Calendar.getInstance()
        time.set(Calendar.HOUR_OF_DAY, hour)
        time.set(Calendar.MINUTE, minute)
        time.set(Calendar.SECOND, 0)
        time.set(Calendar.MILLISECOND, 0)
        return time
    }

    companion object {
        fun create(hour: Int = 8, minute: Int = 0, amount: Int = 1, pillId: Long): Reminder {
            val calendar = Calendar.getInstance()
            calendar.clear()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            return Reminder(calendar = calendar, amount = amount, pillId = pillId)
        }
    }

    val timeString: String
        get() = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.time)

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

