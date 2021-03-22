package eu.vojtechh.takeyourpill.adapter.viewholder

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.ItemHistoryPillBinding
import eu.vojtechh.takeyourpill.klass.context
import eu.vojtechh.takeyourpill.klass.onClick
import eu.vojtechh.takeyourpill.klass.setBackgroundColorShaped
import eu.vojtechh.takeyourpill.model.BaseModel
import eu.vojtechh.takeyourpill.model.HistoryOverallItem
import eu.vojtechh.takeyourpill.model.HistoryPillItem
import eu.vojtechh.takeyourpill.model.Pill

class HistoryViewHolder(
    private val binding: ItemHistoryPillBinding,
    private val clickListener: (View, BaseModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(historyPill: HistoryPillItem) = binding.run {
        when (historyPill.historyType) {
            is HistoryOverallItem -> {
                viewPillColor.isVisible = false
                textHistoryName.text = binding.root.context.getString(R.string.stat_overall)
            }
            is Pill -> {
                viewPillColor.isVisible = true
                viewPillColor.setBackgroundColorShaped(historyPill.historyType.colorResource(context))
                textHistoryName.text = historyPill.historyType.name
            }
        }

        cardHistoryPill.onClick { view ->
            clickListener(view, historyPill.historyType)
        }

        textHistoryDescription.text = historyPill.stat.getSummaryText(context)
        textHistoryDescription.isVisible = historyPill.stat.hasStats
    }
}
