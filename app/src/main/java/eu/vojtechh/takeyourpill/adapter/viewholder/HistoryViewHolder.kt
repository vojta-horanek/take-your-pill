package eu.vojtechh.takeyourpill.adapter.viewholder

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.adapter.AppRecyclerAdapter
import eu.vojtechh.takeyourpill.databinding.ItemHistoryPillBinding
import eu.vojtechh.takeyourpill.klass.isNull
import eu.vojtechh.takeyourpill.model.HistoryOverallItem
import eu.vojtechh.takeyourpill.model.HistoryPillItem
import eu.vojtechh.takeyourpill.model.Pill

class HistoryViewHolder(
        private val binding: ItemHistoryPillBinding,
        private val listener: AppRecyclerAdapter.ItemListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(historyPill: HistoryPillItem) {
        if (historyPill.context is HistoryOverallItem) {
            binding.viewPillColor.isVisible = false
            binding.textHistoryName.text = binding.root.context.getString(R.string.stat_overall)
        } else if (historyPill.context is Pill) {
            binding.pill = historyPill.context
            binding.textHistoryName.text = historyPill.context.name
        }
        binding.cardHistoryPill.setOnClickListener { v -> listener.onItemClicked(v, historyPill.context) }

        binding.stat = historyPill.stat
        binding.textHistoryDescription.isVisible = historyPill.stat.isNull()

        binding.executePendingBindings()
    }
}
