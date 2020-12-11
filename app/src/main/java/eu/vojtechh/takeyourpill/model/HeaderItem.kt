package eu.vojtechh.takeyourpill.model

class HeaderItem(val title: String) : GeneralRecyclerItem() {
    override val itemType = ItemTypes.HEADER
}