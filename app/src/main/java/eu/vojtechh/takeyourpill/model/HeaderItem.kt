package eu.vojtechh.takeyourpill.model

class HeaderItem(val title: String) : GeneralRecyclerItem() {
    override val itemType = ItemTypes.HEADER
    override fun isSame(newItem: GeneralRecyclerItem) = true
    override fun isContentSame(newItem: GeneralRecyclerItem) = true
}