package eu.vojtechh.takeyourpill.model

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.room.Embedded
import androidx.room.Ignore
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
) : GeneralRecyclerItem() {

    @Ignore
    override val itemType: ItemTypes = ItemTypes.PILL

    companion object {

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

    fun getNotificationDescription(context: Context, reminder: Reminder) =
        context.resources.getQuantityString(
            R.plurals.it_is_time_to_take,
            reminder.amount,
            reminder.amount,
            reminder.timeString
        )

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
    val descriptionVisibility
        get() = pill.description?.let { if (it.isNotBlank()) View.VISIBLE else View.GONE }
            ?: View.GONE

    fun getCloseReminder(): Reminder? {
        // TODO Also include if user confirmed already
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        calendar.clear()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val timeOffset = (10 /* minutes */ * 60 * 1000)
        reminders.forEach {
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

    fun getRemindersString(context: Context): String {
        val sorted = reminders.sortedBy { rem -> rem.calendar.time }.toMutableList()
        return sorted.joinToString {
            context.resources.getString(
                R.string.pill_time_reminders_format,
                it.amount, it.timeString
            )
        }
    }

    fun photoDrawable(context: Context) =
        if (pill.photo != null) BitmapDrawable(context.resources, pill.photo)
        else ContextCompat.getDrawable(context, R.drawable.photo_default)

    fun colorResource(context: Context) = pill.color.getColor(context)

}