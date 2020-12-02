package eu.vojtechh.takeyourpill.adapter

import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.databinding.ItemReminderBinding
import eu.vojtechh.takeyourpill.model.Reminder

class ReminderViewHolder(
    private val binding: ItemReminderBinding,
    listener: ReminderAdapter.ReminderAdapterListener
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.listener = listener
    }

    fun bind(reminder: Reminder) {
        binding.reminder = reminder
        binding.executePendingBindings()
    }
}
