package eu.vojtechh.takeyourpill.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.ItemHeaderBinding
import eu.vojtechh.takeyourpill.databinding.ItemPillBinding
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.PillColor
import eu.vojtechh.takeyourpill.reminder.ReminderOptions

class PillAdapter(
    private val listener: PillAdapterListener,
    private val sectionPrefix: String
) : ListAdapter<Pill, RecyclerView.ViewHolder>(Pill.DiffCallback) {

    interface PillAdapterListener {
        fun onPillClicked(view: View, pill: Pill)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == 1) {
            if (holder is HeaderViewHolder) {
                holder.bind(sectionPrefix)
            }
        } else if (holder.itemViewType == 2) {
            if (holder is PillViewHolder) {
                holder.bind(getItem(position))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> HeaderViewHolder(
                ItemHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            2 -> PillViewHolder(
                ItemPillBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                listener
            )
            else -> throw RuntimeException("Unknown view holder")
        }
    }

    override fun submitList(list: List<Pill>?) {
        // Yes, this is for the header, yes it is awful
        list?.let {
            val newList = list.toMutableList()
            newList.add(
                0,
                Pill(
                    "HEADER",
                    null,
                    null,
                    PillColor(R.color.colorBlue),
                    ReminderOptions.Empty(),
                    ReminderOptions.Empty()
                )
            )
            super.submitList(newList)
        } ?: super.submitList(list)

    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) 1 else 2
    }
}