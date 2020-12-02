package eu.vojtechh.takeyourpill.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.util.ObjectsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.reminder.ReminderOptions

@Entity(tableName = "pill")
data class Pill(
    var name: String,
    var description: String?,
    var photo: Bitmap?,
    var color: PillColor,
    @Embedded(prefix = "constant_") var remindConstant: ReminderOptions,
    @Embedded(prefix = "current_") var remindCurrent: ReminderOptions,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
) {
    val photoVisibility
        get() = if (photo != null) View.VISIBLE else View.GONE
    val colorVisibility
        get() = if (photo != null) View.GONE else View.VISIBLE
    val descriptionVisibility
        get() = description?.let { if (it.isNotBlank()) View.VISIBLE else View.GONE } ?: View.GONE

    fun photoDrawable(context: Context) =
        if (photo != null) BitmapDrawable(context.resources, photo)
        else ContextCompat.getDrawable(context, R.drawable.photo_default)

    fun colorResource(context: Context) = color.getColor(context)

    object DiffCallback : DiffUtil.ItemCallback<Pill>() {
        override fun areItemsTheSame(oldItem: Pill, newItem: Pill) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Pill, newItem: Pill) =
            ObjectsCompat.equals(oldItem, newItem)
    }
}