package eu.vojtechh.takeyourpill.model

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.klass.isNotNull
import eu.vojtechh.takeyourpill.klass.isNull

data class Pill(
    @Embedded val pillEntity: PillEntity,
    @Relation(
        parentColumn = "pillId",
        entityColumn = "pillId"
    )
    var reminders: List<Reminder>
) : BaseModel() {

    @Ignore
    override var itemType: ItemType = ItemType.PILL

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
        get() = if (pillEntity.options.isIndefinite()) {
            null
        } else {
            pillEntity.options.lastReminderDate
        }
        set(value) {
            if (pillEntity.options.isIndefinite()) {
                pillEntity.options.lastReminderDate = null
            } else {
                pillEntity.options.lastReminderDate = value
            }
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
            val isPhotoSame =
                if (this.photo.isNull() && newItem.photo.isNotNull()) {
                    false
                } else if (this.photo.isNotNull() && newItem.photo.isNull()) {
                    false
                } else if (this.photo.isNotNull() && newItem.photo.isNotNull()) {
                    this.photo?.sameAs(newItem.photo) ?: false
                } else {
                    true
                }

            this.name == newItem.name &&
                    this.description == newItem.description &&
                    isPhotoSame &&
                    this.color.resource == newItem.color.resource &&
                    this.deleted == newItem.deleted &&
                    this.options.isSame(newItem.options) &&
                    this.reminders.containsAll(newItem.reminders) &&
                    newItem.reminders.containsAll(this.reminders) &&
                    this.closeHistory == newItem.closeHistory
        } else false
}
