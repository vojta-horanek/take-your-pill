package eu.vojtechh.takeyourpill.adapter.viewholder

import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.databinding.LayoutViewEmptyBinding

class EmptyViewHolder(
    val binding: LayoutViewEmptyBinding,
    val description: String,
    val drawable: Drawable?
) : RecyclerView.ViewHolder(binding.root) {
    fun bind() = binding.run {
        textEmptyDescription.text = description
        imageEmpty.setImageDrawable(drawable)
    }
}