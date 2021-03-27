package eu.vojtechh.takeyourpill.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pill")
data class PillEntity(
    var name: String,
    var description: String?,
    var photo: Bitmap?,
    var color: PillColor,
    var deleted: Boolean = false,
    @Embedded var options: ReminderOptions,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "pillId") val id: Long = 0
)