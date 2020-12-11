package eu.vojtechh.takeyourpill.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import eu.vojtechh.takeyourpill.databinding.ItemReminderBinding
import eu.vojtechh.takeyourpill.model.Reminder

class ReminderAdapter(
    private val listener: ReminderAdapterListener,
    private val showDelete: Boolean = true,
    private val showRipple: Boolean = true
) : ListAdapter<Reminder, ReminderViewHolder>(Reminder.DiffCallback) {

    interface ReminderAdapterListener {
        fun onReminderDelete(view: View, reminder: Reminder) {}
        fun onReminderClicked(view: View, reminder: Reminder) {}
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(getItem(position), showDelete, showRipple)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        return ReminderViewHolder(
            ItemReminderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )
    }
}