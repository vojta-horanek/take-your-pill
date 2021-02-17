package eu.vojtechh.takeyourpill.model

data class HistoryPillItem(
        val pill: Pill,
        val stat: StatItem
) : BaseModel() {
    override fun isSame(newItem: BaseModel) = pill.isSame(newItem)
    override fun isContentSame(newItem: BaseModel) = pill.isContentSame(newItem)
    override val itemType: ItemTypes
        get() = pill.itemType
}
