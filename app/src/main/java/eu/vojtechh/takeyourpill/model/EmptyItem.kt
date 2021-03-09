package eu.vojtechh.takeyourpill.model

class EmptyItem : BaseModel() {
    override val itemType = ItemTypes.EMPTY
    override fun isSame(newItem: BaseModel) = false
    override fun isContentSame(newItem: BaseModel) = false
}
