package eu.vojtechh.takeyourpill.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import eu.vojtechh.takeyourpill.databinding.ItemColorBinding
import eu.vojtechh.takeyourpill.model.PillColor

class ColorAdapter(
    private val listener: ColorAdapterListener
) : ListAdapter<PillColor, ColorViewHolder>(PillColor.DiffCallback) {

    interface ColorAdapterListener {
        fun onColorClicked(view: View, color: PillColor)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder(
            ItemColorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )
    }
}