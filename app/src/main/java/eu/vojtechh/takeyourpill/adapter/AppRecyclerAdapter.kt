package eu.vojtechh.takeyourpill.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.adapter.viewholder.EmptyViewHolder
import eu.vojtechh.takeyourpill.adapter.viewholder.HeaderViewHolder
import eu.vojtechh.takeyourpill.adapter.viewholder.HistoryViewHolder
import eu.vojtechh.takeyourpill.adapter.viewholder.PillViewHolder
import eu.vojtechh.takeyourpill.databinding.ItemHeaderBinding
import eu.vojtechh.takeyourpill.databinding.ItemHistoryPillBinding
import eu.vojtechh.takeyourpill.databinding.ItemPillBinding
import eu.vojtechh.takeyourpill.databinding.LayoutViewEmptyBinding
import eu.vojtechh.takeyourpill.model.*

class AppRecyclerAdapter(
    private val listener: ItemListener,
    private val headerText: String,
    private val emptyDescription: String,
    private val emptyDrawable: Drawable?
) : ListAdapter<GeneralRecyclerItem, RecyclerView.ViewHolder>(GeneralRecyclerItem.DiffCallback) {

    interface ItemListener {
        fun onItemClicked(view: View, item: GeneralRecyclerItem)
        fun onPillConfirmClicked(view: View, reminder: Reminder) {}
        fun onPillNotConfirmClicked(view: View, reminder: Reminder) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            GeneralRecyclerItem.ItemTypes.PILL.ordinal -> if (holder is PillViewHolder) holder.bind(
                getItem(position) as Pill
            )
            GeneralRecyclerItem.ItemTypes.HEADER.ordinal -> if (holder is HeaderViewHolder) holder.bind(
                (getItem(position) as HeaderItem).title
            )
            GeneralRecyclerItem.ItemTypes.HISTORY.ordinal -> if (holder is HistoryViewHolder) holder.bind(
                getItem(position) as Pill
            )
            GeneralRecyclerItem.ItemTypes.EMPTY.ordinal -> if (holder is EmptyViewHolder) holder.bind()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            GeneralRecyclerItem.ItemTypes.PILL.ordinal -> PillViewHolder(
                ItemPillBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                listener
            )
            GeneralRecyclerItem.ItemTypes.HEADER.ordinal -> HeaderViewHolder(
                ItemHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            GeneralRecyclerItem.ItemTypes.EMPTY.ordinal -> EmptyViewHolder(
                LayoutViewEmptyBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                emptyDescription,
                emptyDrawable
            )
            GeneralRecyclerItem.ItemTypes.HISTORY.ordinal -> HistoryViewHolder(
                ItemHistoryPillBinding.inflate(
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
            newList.add(
                0,
                HeaderItem(headerText)
            )
            if (it.isEmpty()) {
                newList.add(EmptyItem())
            }
            super.submitList(newList)
        } ?: super.submitList(list)

    }

    override fun getItemViewType(position: Int) = getItem(position).itemType.ordinal
}