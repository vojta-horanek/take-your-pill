package eu.vojtechh.takeyourpill.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
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
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
) {
    fun hasPhoto() = photo != null
    fun drawable(context: Context) = BitmapDrawable(context.resources, photo)
    fun colorDrawable(context: Context): Drawable? {
        return when (color) {
            PillColor.RED -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.dot_red,
                context.theme
            )
        }
    }
}