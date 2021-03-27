package eu.vojtechh.takeyourpill.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.adapter.viewholder.*
import eu.vojtechh.takeyourpill.databinding.*
import eu.vojtechh.takeyourpill.model.*
import eu.vojtechh.takeyourpill.model.BaseModel.ItemType

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
            ItemType.PILL.ordinal -> if (holder is PillViewHolder) holder.bind(
                getItem(position) as Pill
            )
            ItemType.HEADER.ordinal -> if (holder is HeaderViewHolder) holder.bind(
                (getItem(position) as HeaderItem).title
            )
            ItemType.HISTORY.ordinal -> if (holder is HistoryViewHolder) holder.bind(
                getItem(position) as HistoryPillItem
            )
            ItemType.CHART.ordinal -> if (holder is ChartViewHolder) holder.bind(
                getItem(position) as ChartItem
            )
            ItemType.EMPTY.ordinal -> if (holder is EmptyViewHolder) holder.bind()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ItemType.PILL.ordinal -> PillViewHolder(
                ItemPillBinding.inflate(layoutInflater, parent, false),
                onItemClickListener,
                onPillConfirmClickListener
            )
            ItemType.HEADER.ordinal -> HeaderViewHolder(
                ItemHeaderBinding.inflate(layoutInflater, parent, false)
            )
            ItemType.EMPTY.ordinal -> EmptyViewHolder(
                LayoutViewEmptyBinding.inflate(layoutInflater, parent, false),
                emptyDescription,
                emptyDrawable
            )
            ItemType.HISTORY.ordinal -> HistoryViewHolder(
                ItemHistoryPillBinding.inflate(layoutInflater, parent, false),
                onItemClickListener
            )
            ItemType.CHART.ordinal -> ChartViewHolder(
                ItemChartBinding.inflate(layoutInflater, parent, false),
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