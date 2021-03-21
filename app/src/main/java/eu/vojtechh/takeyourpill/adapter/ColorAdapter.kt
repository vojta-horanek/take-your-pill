package eu.vojtechh.takeyourpill.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import eu.vojtechh.takeyourpill.adapter.viewholder.ColorViewHolder
import eu.vojtechh.takeyourpill.databinding.ItemColorBinding
import eu.vojtechh.takeyourpill.model.PillColor

class ColorAdapter : ListAdapter<PillColor, ColorViewHolder>(PillColor.DiffCallback) {

    private var clickListener: (View, PillColor) -> Unit = { _, _ -> }

    fun setOnColorClickedListener(listener: (View, PillColor) -> Unit) {
        clickListener = listener
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) =
        holder.bind(getItem(position))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ColorViewHolder(
            ItemColorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            clickListener
        )

}