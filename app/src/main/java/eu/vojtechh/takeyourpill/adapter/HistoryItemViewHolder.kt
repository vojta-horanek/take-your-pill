package eu.vojtechh.takeyourpill.adapter

import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.ItemHistoryBinding
import eu.vojtechh.takeyourpill.klass.getDateTimeString
import eu.vojtechh.takeyourpill.model.History

class HistoryItemViewHolder(
    private val binding: ItemHistoryBinding,
    private val listener: HistoryViewAdapter.ItemListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(history: History) {
        with(binding) {
            binding.history = history
            binding.listener = listener
            textHistoryReminded.text = binding.root.context.getString(
                R.string.reminded_at,
                history.historyEntity.reminded.getDateTimeString()
            )
            textHistoryConfirmed.visibility = history.confirmedVisibility
            textHistoryConfirmed.text = binding.root.context.getString(
                R.string.confirmed_at,
                history.historyEntity.confirmed?.getDateTimeString()
            )
            executePendingBindings()
        }
    }
}