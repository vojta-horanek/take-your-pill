package eu.vojtechh.takeyourpill.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import eu.vojtechh.takeyourpill.databinding.PillItemLayoutBinding
import eu.vojtechh.takeyourpill.model.Pill

class PillAdapter(
    private val listener: PillAdapterListener,
    private val sectionPrefix: String
) : ListAdapter<Pill, PillViewHolder>(Pill.DiffCallback) {

    interface PillAdapterListener {
        fun onPillClicked(view: View, pill: Pill)
    }

    override fun onBindViewHolder(holder: PillViewHolder, position: Int) {
        holder.bind(getItem(position), sectionPrefix)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillViewHolder {
        return PillViewHolder(
            PillItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )
    }
}