package eu.vojtechh.takeyourpill.adapter.viewholder

import androidx.core.os.ConfigurationCompat.getLocales
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
    fun bind(history: History) {
        binding.history = history
        binding.listener = listener
        with(binding) {

            textHistoryReminded.text = DateFormat.getTimeInstance(DateFormat.SHORT)
                .format(history.historyEntity.reminded.time)

            textHistoryConfirmed.visibility = history.confirmedVisibility
            frameLayoutConfirm.visibility = history.confirmedVisibility
            textHistoryConfirmed.text = history.historyEntity.confirmed?.let {
                DateFormat.getTimeInstance(DateFormat.SHORT).format(it.time)
            }

            val pattern = "dd. MM."
            val primaryLocale = getLocales(binding.root.context.resources.configuration).get(0)
            val dateFormat = SimpleDateFormat(pattern, primaryLocale)
            textDate.text = dateFormat.format(history.historyEntity.reminded.time)

            executePendingBindings()
        }
    }
}