package eu.vojtechh.takeyourpill.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.databinding.ItemHistoryBinding
import eu.vojtechh.takeyourpill.databinding.LayoutPillViewEmptyBinding
import eu.vojtechh.takeyourpill.model.EmptyItem
import eu.vojtechh.takeyourpill.model.GeneralRecyclerItem
import eu.vojtechh.takeyourpill.model.History

class HistoryViewAdapter(
    private val listener: ItemListener,
) : ListAdapter<GeneralRecyclerItem, RecyclerView.ViewHolder>(GeneralRecyclerItem.DiffCallback) {

    interface ItemListener {
        fun onItemClicked(view: View, item: GeneralRecyclerItem)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            GeneralRecyclerItem.ItemTypes.HISTORY.ordinal -> if (holder is HistoryItemViewHolder) holder.bind(
                getItem(position) as History
            )
            GeneralRecyclerItem.ItemTypes.EMPTY.ordinal -> if (holder is EmptyViewHolder) holder.bind()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            GeneralRecyclerItem.ItemTypes.EMPTY.ordinal -> EmptyViewHolder(
                LayoutPillViewEmptyBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            GeneralRecyclerItem.ItemTypes.HISTORY.ordinal -> HistoryItemViewHolder(
                ItemHistoryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                listener
            )
            else -> throw RuntimeException("Unknown view holder")
        }
    }

    override fun submitList(list: List<GeneralRecyclerItem>?) {
        list?.let {
            val newList = list.toMutableList()
            if (it.isEmpty()) {
                newList.add(EmptyItem())
            }
            super.submitList(newList)
        } ?: super.submitList(list)

    }

    override fun getItemViewType(position: Int) = getItem(position).itemType.ordinal
}