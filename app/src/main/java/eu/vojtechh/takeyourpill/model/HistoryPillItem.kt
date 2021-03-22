package eu.vojtechh.takeyourpill.model

data class HistoryPillItem(
    val historyType: BaseModel,
    val stat: StatItem
) : BaseModel() {
    override fun isSame(newItem: BaseModel) = historyType.isSame(newItem)
    override fun isContentSame(newItem: BaseModel) = historyType.isContentSame(newItem)
    override val itemType: ItemType
        get() = historyType.itemType
}

class HistoryOverallItem : BaseModel() {
    override fun isSame(newItem: BaseModel) = true
    override fun isContentSame(newItem: BaseModel) = false
    override val itemType: ItemType
        get() = ItemType.HISTORY
}