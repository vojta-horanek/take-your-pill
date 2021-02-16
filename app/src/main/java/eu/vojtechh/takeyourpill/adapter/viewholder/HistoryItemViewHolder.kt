package eu.vojtechh.takeyourpill.adapter.viewholder

import androidx.core.os.ConfigurationCompat.getLocales
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.adapter.HistoryViewAdapter
import eu.vojtechh.takeyourpill.databinding.ItemHistoryBinding
import eu.vojtechh.takeyourpill.model.History
import java.text.DateFormat
import java.text.SimpleDateFormat

class HistoryItemViewHolder(
    private val binding: ItemHistoryBinding,
    private val listener: HistoryViewAdapter.ItemListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(history: History, isFirstInList: Boolean, isFirstOfDate: Boolean, position: Int) {
        binding.history = history
        binding.listener = listener
        binding.position = position
        with(binding) {

            textHistoryReminded.text = DateFormat.getTimeInstance(DateFormat.SHORT)
                .format(history.reminded.time)

            textHistoryConfirmed.text = history.confirmed?.let {
                DateFormat.getTimeInstance(DateFormat.SHORT).format(it.time)
            }

            val pattern = "dd. MM."
            val primaryLocale = getLocales(binding.root.context.resources.configuration).get(0)
            val dateFormat = SimpleDateFormat(pattern, primaryLocale)
            textDate.text = dateFormat.format(history.reminded.time)

            textDate.isVisible = isFirstOfDate
            // Should this item show a divider
            divider.isVisible = isFirstOfDate && !isFirstInList

            listOf(frameLayoutAmount, frameLayoutConfirm, frameLayoutReminder).forEach {
                it.isVisible = isFirstInList
            }
            executePendingBindings()
        }
    }
}