package eu.vojtechh.takeyourpill.adapter

import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.databinding.PillItemLayoutBinding
import eu.vojtechh.takeyourpill.model.Pill

class PillViewHolder(
    private val binding: PillItemLayoutBinding,
    listener: PillAdapter.PillAdapterListener
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.listener = listener
    }

    fun bind(pill: Pill) {
        binding.pill = pill
        binding.executePendingBindings()
    }
}