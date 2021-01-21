package eu.vojtechh.takeyourpill.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.databinding.ItemColorBinding
import eu.vojtechh.takeyourpill.model.PillColor

class ColorViewHolder(
    private val binding: ItemColorBinding,
    private val listener: (View, PillColor) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(pillColor: PillColor) {
        binding.run {
            color = pillColor
            pillColorFrame.setOnClickListener { view -> listener(view, pillColor) }
            executePendingBindings()
        }
    }
}
