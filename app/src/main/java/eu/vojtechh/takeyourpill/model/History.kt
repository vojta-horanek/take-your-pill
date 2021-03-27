package eu.vojtechh.takeyourpill.model

import androidx.room.*
import java.util.*

/**
 * Database entity used for storing history
 */

@Entity(
        tableName = "history",
        foreignKeys = [ForeignKey(
                entity = PillEntity::class,
                parentColumns = arrayOf("pillId"),
                childColumns = arrayOf("pillId"),
                onDelete = ForeignKey.CASCADE
        )]
)
data class History(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "historyId")
        val id: Long = 0,

        var reminded: Calendar,

        var confirmed: Calendar? = null,

        val amount: String = "1",

        @ColumnInfo(index = true)
        var pillId: Long
) : BaseModel() {

    @Ignore
    var pillName: String = ""

    val hasBeenConfirmed: Boolean
        get() = confirmed?.let { true } ?: false

    override val itemType: ItemType
        get() = ItemType.HISTORY_ITEM

    override fun isSame(newItem: BaseModel) =
        if (newItem is History) {
            this.id == newItem.id
        } else false

    override fun isContentSame(newItem: BaseModel) =
        if (newItem is History) {
            this.hasBeenConfirmed == newItem.hasBeenConfirmed &&
                    this.confirmed?.timeInMillis == newItem.confirmed?.timeInMillis &&
                    this.amount == newItem.amount
        } else false
}