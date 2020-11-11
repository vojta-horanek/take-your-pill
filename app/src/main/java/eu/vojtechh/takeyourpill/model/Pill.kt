package eu.vojtechh.takeyourpill.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import eu.vojtechh.takeyourpill.reminder.ReminderOptions

@Entity(tableName = "pill")
data class Pill(
    var name: String,
    var description: String?,
    var photo: Bitmap?,
    var color: PillColor,
    @Embedded(prefix = "constant_") var remindConstant: ReminderOptions,
    @Embedded(prefix = "current_") var remindCurrent: ReminderOptions,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
) {
    fun photoVisibility() = if (photo != null) View.VISIBLE else View.GONE
    fun descriptionVisibility() =
        description?.let { if (it.isNotBlank()) View.VISIBLE else View.GONE } ?: View.GONE

    fun photoDrawable(context: Context) = BitmapDrawable(context.resources, photo)
    fun photoDrawableEdit(context: Context) =
        BitmapDrawable(context.resources, photo) //TODO Return default image

    // TODO Use just color resource possibly with ShapeableImageView?
    fun colorDrawable(context: Context) = color.getDrawable(context)

    object DiffCallback : DiffUtil.ItemCallback<Pill>() {
        override fun areItemsTheSame(oldItem: Pill, newItem: Pill) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Pill, newItem: Pill) = oldItem == newItem
    }
}