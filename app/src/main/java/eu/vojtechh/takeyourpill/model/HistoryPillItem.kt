package eu.vojtechh.takeyourpill.model

data class HistoryPillItem(
    val historyType: BaseModel,
    val stat: StatItem
) : BaseModel() {
    override fun isSame(newItem: BaseModel) =
        if (newItem is HistoryPillItem) {
            this.stat.pillId == newItem.stat.pillId
        } else historyType.isSame(newItem)

    override fun isContentSame(newItem: BaseModel) =
        if (newItem is HistoryPillItem) {
            newItem.stat.reminded == this.stat.reminded &&
                    newItem.stat.confirmed == this.stat.confirmed &&
                    newItem.stat.missed == this.stat.missed
        } else historyType.isContentSame(newItem)

    override val itemType: ItemType
        get() = historyType.itemType
}

class HistoryOverallItem : BaseModel() {
    override fun isSame(newItem: BaseModel) = newItem is HistoryOverallItem

    override fun isContentSame(newItem: BaseModel) = false
    override val itemType: ItemType
        get() = ItemType.HISTORY
}