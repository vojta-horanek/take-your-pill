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
import eu.vojtechh.takeyourpill.model.BaseModel.ItemTypes

class AppRecyclerAdapter(
    private val headerText: String?,
    private val emptyDescription: String,
    private val emptyDrawable: Drawable?
) : ListAdapter<BaseModel, RecyclerView.ViewHolder>(BaseModel.DiffCallback) {

    private var onItemClickListener: (View, BaseModel) -> Unit = { _, _ -> }
    private var onPillConfirmClickListener: (History) -> Unit = { }

    fun setOnItemClickListener(listener: (View, BaseModel) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnPillConfirmClickListener(listener: (History) -> Unit) {
        onPillConfirmClickListener = listener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ItemTypes.PILL.ordinal -> if (holder is PillViewHolder) holder.bind(
                getItem(position) as Pill
            )
            ItemTypes.HEADER.ordinal -> if (holder is HeaderViewHolder) holder.bind(
                (getItem(position) as HeaderItem).title
            )
            ItemTypes.HISTORY.ordinal -> if (holder is HistoryViewHolder) holder.bind(
                getItem(position) as HistoryPillItem
            )
            ItemTypes.EMPTY.ordinal -> if (holder is EmptyViewHolder) holder.bind()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ItemTypes.PILL.ordinal -> PillViewHolder(
                ItemPillBinding.inflate(layoutInflater, parent, false),
                onItemClickListener,
                onPillConfirmClickListener
            )
            ItemTypes.HEADER.ordinal -> HeaderViewHolder(
                ItemHeaderBinding.inflate(layoutInflater, parent, false)
            )
            ItemTypes.EMPTY.ordinal -> EmptyViewHolder(
                LayoutViewEmptyBinding.inflate(layoutInflater, parent, false),
                emptyDescription,
                emptyDrawable
            )
            ItemTypes.HISTORY.ordinal -> HistoryViewHolder(
                ItemHistoryPillBinding.inflate(layoutInflater, parent, false),
                onItemClickListener
            )
            else -> throw RuntimeException("Unknown ViewHolder")
        }
    }

    override fun submitList(list: List<BaseModel>?) = submitList(list, null)

    override fun submitList(list: List<BaseModel>?, commitCallback: Runnable?) {
        list?.let { ll ->
            val newList = ll.toMutableList()
            headerText?.let { newList.add(0, HeaderItem(it)) }
            if (ll.isEmpty()) {
                newList.add(EmptyItem())
            }
            super.submitList(newList, commitCallback)
        } ?: super.submitList(list, commitCallback)
    }

    override fun getItemViewType(position: Int) = getItem(position).itemType.ordinal
}