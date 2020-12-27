package eu.vojtechh.takeyourpill.model

import androidx.room.Embedded
import androidx.room.Relation

data class History(
    @Embedded val history: BaseHistory,
    @Relation(
        parentColumn = "reminderId",
        entityColumn = "reminderId"
    )
    var reminder: Reminder
) : GeneralRecyclerItem() {
    val hasBeenConfirmed: Boolean
        get() = history.hasBeenConfirmed

    override val itemType: ItemTypes
        get() = ItemTypes.HISTORY
}