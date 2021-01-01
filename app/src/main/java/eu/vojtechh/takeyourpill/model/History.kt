package eu.vojtechh.takeyourpill.model

import android.view.View
import androidx.room.Embedded
import androidx.room.Relation

/**
 * data class for mapping [HistoryEntity] together with [Pill]
 */

data class History(
    @Embedded val historyEntity: HistoryEntity,
    @Relation(
        parentColumn = "pillId",
        entityColumn = "pillId"
    )
    var pill: PillEntity
) : GeneralRecyclerItem() {
    val hasBeenConfirmed: Boolean
        get() = historyEntity.hasBeenConfirmed

    override val itemType: ItemTypes
        get() = ItemTypes.HISTORY_ITEM

    override fun isSame(newItem: GeneralRecyclerItem) =
        if (newItem is History) {
            this.historyEntity.id == newItem.historyEntity.id
        } else false

    override fun isContentSame(newItem: GeneralRecyclerItem) =
        if (newItem is History) {
            this.hasBeenConfirmed == newItem.hasBeenConfirmed
        } else false

    val confirmedVisibility
        get() = if (hasBeenConfirmed) View.VISIBLE else View.GONE
}