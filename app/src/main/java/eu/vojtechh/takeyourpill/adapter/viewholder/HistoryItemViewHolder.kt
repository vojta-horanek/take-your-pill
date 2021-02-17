package eu.vojtechh.takeyourpill.adapter.viewholder

import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.adapter.HistoryViewAdapter
import eu.vojtechh.takeyourpill.databinding.ItemHistoryBinding
import eu.vojtechh.takeyourpill.klass.setDateText
import eu.vojtechh.takeyourpill.klass.setTimeText
import eu.vojtechh.takeyourpill.klass.setVerticalBias
import eu.vojtechh.takeyourpill.model.History

class HistoryItemViewHolder(
        private val binding: ItemHistoryBinding,
        private val listener: HistoryViewAdapter.ItemListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(history: History, isFirstInList: Boolean, isFirstOfDate: Boolean, position: Int,
             showNames: Boolean) {
        binding.history = history
        binding.listener = listener
        binding.position = position
        with(binding) {

            textHistoryReminded.setTimeText(history.reminded.time)
            history.confirmed?.let {
                textHistoryConfirmed.setTimeText(it.time)
            }

            textDate.setDateText(history.reminded.time)

            textPillName.text = history.pillName
            textPillName.isVisible = showNames

            // Should this item show a divider
            divider.isVisible = isFirstOfDate || isFirstInList
            dividerPill.isVisible = !isFirstOfDate

            if (showNames) {
                textHistoryConfirmed.setVerticalBias(1.0f)
                textDate.isVisible = isFirstOfDate
            } else {
                textHistoryConfirmed.setVerticalBias(0.5f)
                textDate.isInvisible = !isFirstOfDate
            }

            listOf(imageAmount, imageConfirm, imageReminder).forEach {
                it.isVisible = isFirstInList
            }
            executePendingBindings()
        }
    }
}