package eu.vojtechh.takeyourpill.model

import androidx.core.util.ObjectsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Ignore

abstract class GeneralRecyclerItem {

    enum class ItemTypes {
        PILL,
        HEADER,
        EMPTY,
    }


    @Ignore
    open val itemType = ItemTypes.PILL

    // Add more implementations here
    // It would be better, if each item could provide it's own implementation but I can't
    // figure out a way to do it
    object DiffCallback : DiffUtil.ItemCallback<GeneralRecyclerItem>() {
        override fun areItemsTheSame(
            oldItem: GeneralRecyclerItem,
            newItem: GeneralRecyclerItem
        ): Boolean {
            return if (oldItem is Pill && newItem is Pill) {
                oldItem.pill.pillId == newItem.pill.pillId
            } else !(oldItem is HeaderItem && newItem is HeaderItem)
        }

        override fun areContentsTheSame(
            oldItem: GeneralRecyclerItem,
            newItem: GeneralRecyclerItem
        ): Boolean {
            return if (oldItem is Pill && newItem is Pill) {
                ObjectsCompat.equals(oldItem, newItem)
            } else !(oldItem is HeaderItem && newItem is HeaderItem)
        }
    }
}