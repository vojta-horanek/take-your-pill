package eu.vojtechh.takeyourpill.model

import android.view.View
import androidx.room.Embedded
import androidx.room.Relation

data class History(
    @Embedded val historyEntity: HistoryEntity,
    @Relation(
        parentColumn = "reminderId",
        entityColumn = "reminderId"
    )
    var reminder: Reminder
) : GeneralRecyclerItem() {
    val hasBeenConfirmed: Boolean
        get() = historyEntity.hasBeenConfirmed

    override val itemType: ItemTypes
        get() = ItemTypes.HISTORY

    val confirmedVisibility
        get() = if (hasBeenConfirmed) View.VISIBLE else View.GONE
}