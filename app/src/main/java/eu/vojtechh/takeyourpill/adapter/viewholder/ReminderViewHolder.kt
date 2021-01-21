package eu.vojtechh.takeyourpill.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.databinding.ItemReminderBinding
import eu.vojtechh.takeyourpill.model.Reminder

class ReminderViewHolder(
    private val binding: ItemReminderBinding,
    private val clickListener: (View, Reminder) -> Unit,
    private val deleteListener: (View, Reminder) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(reminder: Reminder, showDelete: Boolean, showRipple: Boolean) {
        binding.run {
            this.reminder = reminder
            if (!showDelete) {
                buttonDeleteReminder.visibility = View.GONE
            }
            if (!showRipple) {
                reminderContainer.background = null
            }
            reminderContainer.setOnClickListener { view -> clickListener(view, reminder) }
            buttonDeleteReminder.setOnClickListener { view ->
                deleteListener(
                    view,
                    reminder
                )
            }
            executePendingBindings()
        }
    }
}
