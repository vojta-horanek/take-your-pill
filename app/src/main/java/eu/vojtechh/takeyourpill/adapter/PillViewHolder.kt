package eu.vojtechh.takeyourpill.adapter

import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.databinding.ItemPillBinding
import eu.vojtechh.takeyourpill.model.Pill

class PillViewHolder(
    private val binding: ItemPillBinding,
    listener: PillAdapter.PillAdapterListener
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.listener = listener
    }

    fun bind(pill: Pill) {
        binding.pill = pill
        binding.transitionId = "${pill.id}"
        binding.executePendingBindings()
    }
}