package eu.vojtechh.takeyourpill.adapter

import android.view.View
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

    fun bind(reminder: Reminder, showDelete: Boolean, showRipple: Boolean) {
        binding.reminder = reminder
        if (!showDelete) {
            binding.buttonDeleteReminder.visibility = View.GONE
        }
        if (!showRipple) {
            binding.reminderContainer.background = null
        }
        binding.executePendingBindings()
    }
}
