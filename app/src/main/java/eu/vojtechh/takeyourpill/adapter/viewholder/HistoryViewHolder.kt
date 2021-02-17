package eu.vojtechh.takeyourpill.adapter.viewholder

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.adapter.AppRecyclerAdapter
import eu.vojtechh.takeyourpill.databinding.ItemHistoryPillBinding
import eu.vojtechh.takeyourpill.klass.isNull
import eu.vojtechh.takeyourpill.model.HistoryPillItem

class HistoryViewHolder(
    private val binding: ItemHistoryPillBinding,
    private val listener: AppRecyclerAdapter.ItemListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(historyPill: HistoryPillItem) {
        binding.pill = historyPill.pill
        binding.stat = historyPill.stat
        binding.textHistoryDescription.isVisible = historyPill.stat.isNull()

        binding.listener = listener
        binding.executePendingBindings()
    }
}
