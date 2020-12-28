package eu.vojtechh.takeyourpill.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.adapter.AppRecyclerAdapter
import eu.vojtechh.takeyourpill.databinding.ItemHistoryPillBinding
import eu.vojtechh.takeyourpill.model.Pill

class HistoryViewHolder(
    private val binding: ItemHistoryPillBinding,
    private val listener: AppRecyclerAdapter.ItemListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(pill: Pill) {
        binding.pill = pill
        binding.listener = listener
        binding.executePendingBindings()
    }
}