package eu.vojtechh.takeyourpill.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.databinding.ItemHeaderBinding
import eu.vojtechh.takeyourpill.databinding.ItemPillBinding
import eu.vojtechh.takeyourpill.databinding.LayoutPillViewEmptyBinding
import eu.vojtechh.takeyourpill.model.Pill

class PillAdapter(
    private val listener: PillAdapterListener,
    private val sectionPrefix: String
) : ListAdapter<Pill, RecyclerView.ViewHolder>(Pill.DiffCallback) {

    interface PillAdapterListener {
        fun onPillClicked(view: View, pill: Pill)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            Pill.VIEW_TYPE_ITEM -> if (holder is PillViewHolder) holder.bind(getItem(position))
            Pill.VIEW_TYPE_HEADER -> if (holder is HeaderViewHolder) holder.bind(sectionPrefix)
            Pill.VIEW_TYPE_EMPTY -> if (holder is EmptyViewHolder) holder.bind()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Pill.VIEW_TYPE_ITEM -> PillViewHolder(
                ItemPillBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                listener
            )
            Pill.VIEW_TYPE_HEADER -> HeaderViewHolder(
                ItemHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            Pill.VIEW_TYPE_EMPTY -> EmptyViewHolder(
                LayoutPillViewEmptyBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw RuntimeException("Unknown view holder")
        }
    }

    override fun submitList(list: List<Pill>?) {
        // Yes, this is for the header, yes it is awful FIXME probably
        list?.let {
            val newList = list.toMutableList()
            newList.add(
                0,
                Pill.withHeaderViewType()
            )

            if (it.isEmpty()) {
                newList.add(Pill.withEmptyViewType())
            }

            super.submitList(newList)
        } ?: super.submitList(list)

    }

    override fun getItemViewType(position: Int) = getItem(position).pill.viewType
}