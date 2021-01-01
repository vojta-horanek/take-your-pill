package eu.vojtechh.takeyourpill.model

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Ignore

abstract class GeneralRecyclerItem {

    enum class ItemTypes {
        PILL,
        HEADER,
        EMPTY,
        HISTORY,
        HISTORY_ITEM,
    }

    @Ignore
    open val itemType = ItemTypes.PILL

    abstract fun isSame(newItem: GeneralRecyclerItem): Boolean
    abstract fun isContentSame(newItem: GeneralRecyclerItem): Boolean

    object DiffCallback : DiffUtil.ItemCallback<GeneralRecyclerItem>() {
        override fun areItemsTheSame(
            oldItem: GeneralRecyclerItem,
            newItem: GeneralRecyclerItem
        ): Boolean {
            return oldItem.isSame(newItem)
        }

        override fun areContentsTheSame(
            oldItem: GeneralRecyclerItem,
            newItem: GeneralRecyclerItem
        ): Boolean {
            return oldItem.isContentSame(newItem)
        }
    }
}