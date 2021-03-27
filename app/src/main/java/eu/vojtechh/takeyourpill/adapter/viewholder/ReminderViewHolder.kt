package eu.vojtechh.takeyourpill.adapter.viewholder

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.databinding.ItemReminderBinding
import eu.vojtechh.takeyourpill.klass.context
import eu.vojtechh.takeyourpill.klass.onClick
import eu.vojtechh.takeyourpill.model.Reminder

class ReminderViewHolder(
    private val binding: ItemReminderBinding,
    private val clickListener: (View, Reminder) -> Unit,
    private val deleteListener: (View, Reminder) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(reminder: Reminder, showDelete: Boolean, showRipple: Boolean) = binding.run {
        textReminderTime.text = reminder.formattedString(context)

        buttonDeleteReminder.isVisible = showDelete
        if (!showRipple) reminderContainer.background = null

        reminderContainer.onClick { view -> clickListener(view, reminder) }
        buttonDeleteReminder.onClick { view -> deleteListener(view, reminder) }
    }
}
