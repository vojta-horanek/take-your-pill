package eu.vojtechh.takeyourpill.model

data class HistoryPillItem(
        val context: BaseModel,
        val stat: StatItem
) : BaseModel() {
    override fun isSame(newItem: BaseModel) = context.isSame(newItem)
    override fun isContentSame(newItem: BaseModel) = context.isContentSame(newItem)
    override val itemType: ItemTypes
        get() = context.itemType
}

class HistoryOverallItem : BaseModel() {
    override fun isSame(newItem: BaseModel) = true
    override fun isContentSame(newItem: BaseModel) = false
    override val itemType: ItemTypes
        get() = ItemTypes.HISTORY
}