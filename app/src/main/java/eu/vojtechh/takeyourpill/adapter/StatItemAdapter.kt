package eu.vojtechh.takeyourpill.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import eu.vojtechh.takeyourpill.adapter.viewholder.StatModelViewHolder
import eu.vojtechh.takeyourpill.databinding.ItemStatBinding
import eu.vojtechh.takeyourpill.model.StatItem

class StatItemAdapter : ListAdapter<StatItem, StatModelViewHolder>(StatItem.DiffCallback) {

    override fun onBindViewHolder(holder: StatModelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatModelViewHolder =
        StatModelViewHolder(
            ItemStatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
}