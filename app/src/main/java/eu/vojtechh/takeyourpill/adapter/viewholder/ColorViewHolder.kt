package eu.vojtechh.takeyourpill.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.adapter.ColorAdapter
import eu.vojtechh.takeyourpill.databinding.ItemColorBinding
import eu.vojtechh.takeyourpill.model.PillColor

class ColorViewHolder(
    private val binding: ItemColorBinding,
    listener: ColorAdapter.ColorAdapterListener
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.listener = listener
    }

    fun bind(color: PillColor) {
        binding.color = color
        binding.executePendingBindings()
    }
}
