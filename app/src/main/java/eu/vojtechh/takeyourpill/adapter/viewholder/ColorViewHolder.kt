package eu.vojtechh.takeyourpill.adapter.viewholder

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.databinding.ItemColorBinding
import eu.vojtechh.takeyourpill.klass.context
import eu.vojtechh.takeyourpill.klass.onClick
import eu.vojtechh.takeyourpill.klass.setBackgroundColorShaped
import eu.vojtechh.takeyourpill.model.PillColor

class ColorViewHolder(
    private val binding: ItemColorBinding,
    private val listener: (View, PillColor) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(pillColor: PillColor) = binding.run {
        viewPillColor.setBackgroundColorShaped(pillColor.getColor(context))
        check.isVisible = pillColor.isChecked
        pillColorFrame.onClick { view -> listener(view, pillColor) }
    }
}
