package eu.vojtechh.takeyourpill.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.databinding.ItemStatBinding
import eu.vojtechh.takeyourpill.model.StatItem

class StatModelViewHolder(
    private val binding: ItemStatBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(statItem: StatItem) {
        binding.run {
            stat = statItem
            statItem.color?.let {
                statPillName.setTextColor(it)
            }
            executePendingBindings()
        }
    }
}
