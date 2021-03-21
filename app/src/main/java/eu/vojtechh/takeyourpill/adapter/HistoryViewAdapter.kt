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
import eu.vojtechh.takeyourpill.model.BaseModel.ItemTypes
import eu.vojtechh.takeyourpill.model.EmptyItem
import eu.vojtechh.takeyourpill.model.History
import java.util.*

class HistoryViewAdapter(
        private val listener: ItemListener,
        private val emptyDrawable: Drawable?,
        private val showNames: Boolean
) : ListAdapter<BaseModel, RecyclerView.ViewHolder>(BaseModel.DiffCallback) {

    interface ItemListener {
        fun onItemOptionsClick(view: View, item: BaseModel, position: Int)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ItemTypes.HISTORY_ITEM.ordinal -> {
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
                    holder.bind(
                        currentHistoryItem,
                        currentList.firstOrNull() == currentHistoryItem,
                        isFirstOfDate,
                        position,
                        showNames
                    )
                }
            }
            ItemTypes.EMPTY.ordinal -> if (holder is EmptyViewHolder) holder.bind()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ItemTypes.EMPTY.ordinal -> EmptyViewHolder(
                LayoutViewEmptyBinding.inflate(layoutInflater, parent, false),
                "", emptyDrawable
            )
            ItemTypes.HISTORY_ITEM.ordinal -> HistoryItemViewHolder(
                ItemHistoryBinding.inflate(layoutInflater, parent, false),
                listener
            )
            else -> throw RuntimeException("Unknown ViewHolder")
        }
    }

    override fun submitList(list: List<BaseModel>?) = submitList(list, null)

    override fun submitList(list: List<BaseModel>?, commitCallback: Runnable?) {
        list?.let { ll ->
            if (ll.isEmpty()) {
                val newList = list.toMutableList()
                newList.add(EmptyItem())
                super.submitList(newList, commitCallback)
            } else {
                super.submitList(list, commitCallback)
            }
        } ?: super.submitList(list, commitCallback)
    }

    override fun getItemViewType(position: Int) = getItem(position).itemType.ordinal
}