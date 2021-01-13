package eu.vojtechh.takeyourpill.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.adapter.viewholder.EmptyViewHolder
import eu.vojtechh.takeyourpill.adapter.viewholder.HistoryItemViewHolder
import eu.vojtechh.takeyourpill.databinding.ItemHistoryBinding
import eu.vojtechh.takeyourpill.databinding.LayoutViewEmptyBinding
import eu.vojtechh.takeyourpill.klass.getItemOrNull
import eu.vojtechh.takeyourpill.model.BaseModel
import eu.vojtechh.takeyourpill.model.EmptyItem
import eu.vojtechh.takeyourpill.model.History
import java.util.*

class HistoryViewAdapter(
    private val listener: ItemListener,
    private val emptyDrawable: Drawable?,
) : ListAdapter<BaseModel, RecyclerView.ViewHolder>(BaseModel.DiffCallback) {

    interface ItemListener {
        fun onItemOptionsClick(view: View, item: BaseModel)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            BaseModel.ItemTypes.HISTORY_ITEM.ordinal -> {
                if (holder is HistoryItemViewHolder) {
                    val currentHistoryItem = getItem(position) as History
                    var isFirstOfDate = true
                    (getItemOrNull(position - 1) as History?)?.let {
                        val day = it.reminded.get(Calendar.DAY_OF_YEAR)
                        val dayCurrent =
                            currentHistoryItem.reminded.get(Calendar.DAY_OF_YEAR)
                        if (dayCurrent == day) {
                            isFirstOfDate = false
                        }
                    }
                    holder.bind(currentHistoryItem, position == 0, isFirstOfDate)
                }
            }
            BaseModel.ItemTypes.EMPTY.ordinal -> if (holder is EmptyViewHolder) holder.bind()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            BaseModel.ItemTypes.EMPTY.ordinal -> EmptyViewHolder(
                LayoutViewEmptyBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                "", emptyDrawable
            )
            BaseModel.ItemTypes.HISTORY_ITEM.ordinal -> HistoryItemViewHolder(
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

    override fun submitList(list: List<BaseModel>?) {
        list?.let {
            if (it.isEmpty()) {
                val newList = list.toMutableList()
                newList.add(EmptyItem())
                super.submitList(newList)
            } else {
                super.submitList(list)
            }
        } ?: super.submitList(list)

    }

    override fun getItemViewType(position: Int) = getItem(position).itemType.ordinal
}