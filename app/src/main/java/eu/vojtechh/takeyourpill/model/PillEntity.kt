package eu.vojtechh.takeyourpill.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import eu.vojtechh.takeyourpill.reminder.ReminderOptions

@Entity(tableName = "pill")
data class PillEntity(
    var name: String,
    var description: String?,
    var photo: Bitmap?,
    var color: PillColor,
    var deleted: Boolean = false,
    @Embedded(prefix = "constant_") var options: ReminderOptions,
    @Embedded(prefix = "current_") var optionsChanging: ReminderOptions,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "pillId") val id: Long = 0
)