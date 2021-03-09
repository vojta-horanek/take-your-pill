package eu.vojtechh.takeyourpill.model

class HeaderItem(val title: String) : BaseModel() {
    override val itemType = ItemTypes.HEADER
    override fun isSame(newItem: BaseModel) = true
    override fun isContentSame(newItem: BaseModel) = true
}