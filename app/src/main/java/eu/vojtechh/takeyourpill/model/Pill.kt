package eu.vojtechh.takeyourpill.model

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.util.ObjectsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Embedded
import androidx.room.Relation
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.reminder.ReminderOptions
import java.util.*

data class Pill(
    @Embedded val pill: BasePill,
    @Relation(
        parentColumn = "pillId",
        entityColumn = "pillId"
    )
    var reminders: List<Reminder>
) {

    companion object {

        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_HEADER = 1
        const val VIEW_TYPE_EMPTY = 2

        fun getEmpty() = Pill(
            BasePill(
                "",
                "",
                null,
                PillColor.default(),
                false,
                ReminderOptions.empty(),
                ReminderOptions.empty()
            ), listOf()
        )

        fun withHeaderViewType() = Pill(
            BasePill(
                "",
                "",
                null,
                PillColor.default(),
                false,
                ReminderOptions.empty(),
                ReminderOptions.empty(),
            ).apply { viewType = VIEW_TYPE_HEADER }, listOf()
        )

        fun withEmptyViewType() = Pill(
            BasePill(
                "",
                "",
                null,
                PillColor.default(),
                false,
                ReminderOptions.empty(),
                ReminderOptions.empty(),
            ).apply { viewType = VIEW_TYPE_EMPTY }, listOf()
        )
    }

    var name
        get() = pill.name
        set(value) {
            pill.name = value
        }

    var description
        get() = pill.description
        set(value) {
            pill.description = value
        }

    var photo
        get() = pill.photo
        set(value) {
            pill.photo = value
        }

    var color
        get() = pill.color
        set(value) {
            pill.color = value
        }

    var deleted
        get() = pill.deleted
        set(value) {
            pill.deleted = value
        }

    val id
        get() = pill.pillId

    var options
        get() = pill.options
        set(value) {
            pill.options = value
        }

    var optionsChanging
        get() = pill.optionsChanging
        set(value) {
            pill.optionsChanging = value
        }

    val photoVisibility
        get() = if (pill.photo != null) View.VISIBLE else View.GONE
    val colorVisibility
        get() = if (pill.photo != null) View.GONE else View.VISIBLE
    val descriptionVisibility
        get() = pill.description?.let { if (it.isNotBlank()) View.VISIBLE else View.GONE }
            ?: View.GONE

    fun getCloseReminder(): Reminder? {
        // TODO Also include if user confirmed already
        reminders.forEach {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            calendar.clear()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            val timeOffset = (10 /* minutes */ * 60 * 1000)
            if (LongRange(
                    calendar.timeInMillis - timeOffset,
                    calendar.timeInMillis + timeOffset
                ).contains(it.calendar.timeInMillis)
            ) {
                return it
            }
        }
        return null
    }

    fun getRemindersString(context: Context) = reminders.joinToString {
        context.resources.getString(
            R.string.pill_time_reminders_format,
            it.amount, it.timeString
        )
    }

    fun photoDrawable(context: Context) =
        if (pill.photo != null) BitmapDrawable(context.resources, pill.photo)
        else ContextCompat.getDrawable(context, R.drawable.photo_default)

    fun colorResource(context: Context) = pill.color.getColor(context)

    object DiffCallback : DiffUtil.ItemCallback<Pill>() {
        override fun areItemsTheSame(oldItem: Pill, newItem: Pill) =
            oldItem.pill.pillId == newItem.pill.pillId

        override fun areContentsTheSame(oldItem: Pill, newItem: Pill) =
            ObjectsCompat.equals(oldItem, newItem)
    }
}