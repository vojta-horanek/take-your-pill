package eu.vojtechh.takeyourpill.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "history",
    foreignKeys = [ForeignKey(
        entity = BasePill::class,
        parentColumns = arrayOf("pillId"),
        childColumns = arrayOf("pillId"),
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = Reminder::class,
            parentColumns = arrayOf("reminderId"),
            childColumns = arrayOf("reminderId"),
            onDelete = ForeignKey.CASCADE
        )]
)
data class History(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "historyId") val id: Long = 0,
    var confirmed: Calendar? = null,
    @ColumnInfo(index = true) var pillId: Long,
    @ColumnInfo(index = true) var reminderId: Long
) {
    val isConfirmed: Boolean
        get() = confirmed?.let { true } ?: false
}