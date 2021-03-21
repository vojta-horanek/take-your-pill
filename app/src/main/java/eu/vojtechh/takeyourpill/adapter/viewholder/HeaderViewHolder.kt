package eu.vojtechh.takeyourpill.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.databinding.ItemHeaderBinding

class HeaderViewHolder(
    private val binding: ItemHeaderBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(title: String) = binding.run {
        headerText.text = title
    }
}