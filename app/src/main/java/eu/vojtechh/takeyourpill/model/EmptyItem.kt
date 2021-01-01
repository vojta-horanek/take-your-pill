package eu.vojtechh.takeyourpill.model

class EmptyItem : GeneralRecyclerItem() {
    override val itemType = ItemTypes.EMPTY
    override fun isSame(newItem: GeneralRecyclerItem) = true
    override fun isContentSame(newItem: GeneralRecyclerItem) = true
}
