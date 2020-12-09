package eu.vojtechh.takeyourpill.adapter

import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.databinding.LayoutPillViewEmptyBinding

class EmptyViewHolder(
    val binding: LayoutPillViewEmptyBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind() {
        binding.executePendingBindings()
    }
}