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
    private val headerText: String?,
    private val emptyDescription: String,
    private val emptyDrawable: Drawable?
) : ListAdapter<BaseModel, RecyclerView.ViewHolder>(BaseModel.DiffCallback) {

    interface ItemListener {
        fun onItemClicked(view: View, item: BaseModel)
        fun onPillConfirmClicked(confirmCard: View, history: History) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            BaseModel.ItemTypes.PILL.ordinal -> if (holder is PillViewHolder) holder.bind(
                getItem(position) as Pill
            )
            BaseModel.ItemTypes.HEADER.ordinal -> if (holder is HeaderViewHolder) holder.bind(
                (getItem(position) as HeaderItem).title
            )
            BaseModel.ItemTypes.HISTORY.ordinal -> if (holder is HistoryViewHolder) holder.bind(
                    getItem(position) as HistoryPillItem
            )
            BaseModel.ItemTypes.EMPTY.ordinal -> if (holder is EmptyViewHolder) holder.bind()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            BaseModel.ItemTypes.PILL.ordinal -> PillViewHolder(
                ItemPillBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                listener
            )
            BaseModel.ItemTypes.HEADER.ordinal -> HeaderViewHolder(
                ItemHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            BaseModel.ItemTypes.EMPTY.ordinal -> EmptyViewHolder(
                LayoutViewEmptyBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                emptyDescription,
                emptyDrawable
            )
            BaseModel.ItemTypes.HISTORY.ordinal -> HistoryViewHolder(
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

    override fun submitList(list: List<BaseModel>?) {
        list?.let {
            val newList = list.toMutableList()
            headerText?.let { header ->
                newList.add(
                    0,
                    HeaderItem(header)
                )
            }
            if (it.isEmpty()) {
                newList.add(EmptyItem())
            }
            super.submitList(newList)
        } ?: super.submitList(list)

    }

    override fun getItemViewType(position: Int) = getItem(position).itemType.ordinal
}