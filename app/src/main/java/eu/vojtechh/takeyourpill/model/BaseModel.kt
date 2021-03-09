package eu.vojtechh.takeyourpill.model

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Ignore

abstract class BaseModel {

    enum class ItemTypes {
        PILL,
        HEADER,
        EMPTY,
        HISTORY,
        HISTORY_ITEM,
    }

    @Ignore
    open val itemType = ItemTypes.PILL

    abstract fun isSame(newItem: BaseModel): Boolean
    abstract fun isContentSame(newItem: BaseModel): Boolean

    object DiffCallback : DiffUtil.ItemCallback<BaseModel>() {
        override fun areItemsTheSame(
            oldItem: BaseModel,
            newItem: BaseModel
        ): Boolean {
            return oldItem.isSame(newItem)
        }

        override fun areContentsTheSame(
            oldItem: BaseModel,
            newItem: BaseModel
        ): Boolean {
            return oldItem.isContentSame(newItem)
        }
    }
}