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
        var time: Calendar,

        var amount: String,

        @ColumnInfo(index = true)
        var pillId: Long,

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "reminderId")
        val id: Long = 0
) {
    val hour
        get() = time.hour

    val minute
        get() = time.minute

    fun hasSameTime(other: Reminder) = hour == other.hour && minute == other.minute

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
        fun create(hour: Int = 8, minute: Int = 0, amount: String = "1", pillId: Long): Reminder {
            val calendar = Calendar.getInstance()
            calendar.clear()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            return Reminder(time = calendar, amount = amount, pillId = pillId)
        }
    }

    val timeString: String
        get() = DateFormat.getTimeInstance(DateFormat.SHORT).format(time.time)

    fun formattedString(context: Context) = context.getString(R.string.reminder_text, timeString, amount)


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

