package eu.vojtechh.takeyourpill.model

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.reminder.ReminderOptions

data class Pill(
    @Embedded val pillEntity: PillEntity,
    @Relation(
        parentColumn = "pillId",
        entityColumn = "pillId"
    )
    var reminders: List<Reminder>
) : BaseModel() {

    @Ignore
    override var itemType: ItemTypes = ItemTypes.PILL

    companion object {

        fun new() = Pill(
            PillEntity(
                "",
                "",
                null,
                PillColor.default(),
                false,
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
        context.getString(
            R.string.it_is_time_to_take,
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

    var lastReminderDate
        get() = pillEntity.options.lastReminderDate
        set(value) {
            pillEntity.options.lastReminderDate = value
        }

    val isPhotoVisible
        get() = pillEntity.photo != null
    val isDescriptionVisible
        get() = pillEntity.description?.isNotBlank() ?: false

    @Ignore
    var closeHistory: History? = null

    fun getPhotoDrawable(context: Context) =
        if (pillEntity.photo != null) BitmapDrawable(context.resources, pillEntity.photo)
        else ContextCompat.getDrawable(context, R.drawable.photo_default)

    fun colorResource(context: Context) = pillEntity.color.getColor(context)

    override fun isSame(newItem: BaseModel) =
        if (newItem is Pill) {
            this.pillEntity.id == newItem.pillEntity.id
        } else false


    override fun isContentSame(newItem: BaseModel) =
        if (newItem is Pill) {
            this.name == newItem.name &&
                    this.description == newItem.description &&
                    this.photo == newItem.photo &&
                    this.color == newItem.color &&
                    this.deleted == newItem.deleted &&
                    this.options == newItem.options &&
                    this.reminders == newItem.reminders &&
                    this.closeHistory == newItem.closeHistory

        } else false


}