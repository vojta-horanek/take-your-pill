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
    @Embedded val pillEntity: PillEntity,
    @Relation(
        parentColumn = "pillId",
        entityColumn = "pillId"
    )
    var reminders: List<Reminder>
) : GeneralRecyclerItem() {

    @Ignore
    override var itemType: ItemTypes = ItemTypes.PILL

    companion object {

        fun getEmpty() = Pill(
            PillEntity(
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
        get() = pillEntity.name
        set(value) {
            pillEntity.name = value
        }

    var description
        get() = pillEntity.description
        set(value) {
            pillEntity.description = value
        }

    fun getNotificationDescription(context: Context, reminder: Reminder) =
        context.resources.getQuantityString(
            R.plurals.it_is_time_to_take,
            reminder.amount,
            reminder.amount
        )

    var photo
        get() = pillEntity.photo
        set(value) {
            pillEntity.photo = value
        }

    var color
        get() = pillEntity.color
        set(value) {
            pillEntity.color = value
        }

    var deleted
        get() = pillEntity.deleted
        set(value) {
            pillEntity.deleted = value
        }

    val id
        get() = pillEntity.id

    var options
        get() = pillEntity.options
        set(value) {
            pillEntity.options = value
        }

    var optionsChanging
        get() = pillEntity.optionsChanging
        set(value) {
            pillEntity.optionsChanging = value
        }

    val photoVisibility
        get() = if (pillEntity.photo != null) View.VISIBLE else View.GONE
    val descriptionVisibility
        get() = pillEntity.description?.let { if (it.isNotBlank()) View.VISIBLE else View.GONE }
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
        if (pillEntity.photo != null) BitmapDrawable(context.resources, pillEntity.photo)
        else ContextCompat.getDrawable(context, R.drawable.photo_default)

    fun colorResource(context: Context) = pillEntity.color.getColor(context)

}