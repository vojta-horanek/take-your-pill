package eu.vojtechh.takeyourpill.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "history",
    foreignKeys = [ForeignKey(
        entity = Reminder::class,
        parentColumns = arrayOf("reminderId"),
        childColumns = arrayOf("reminderId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class BaseHistory(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "historyId") val id: Long = 0,
    var confirmed: Calendar? = null,
    @ColumnInfo(index = true) var reminderId: Long
) {
    val hasBeenConfirmed: Boolean
        get() = confirmed?.let { true } ?: false
}