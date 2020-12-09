package eu.vojtechh.takeyourpill.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.ItemPillBinding
import eu.vojtechh.takeyourpill.model.Pill

class PillViewHolder(
    private val binding: ItemPillBinding,
    listener: PillAdapter.PillAdapterListener
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.listener = listener
    }

    fun bind(pill: Pill) {
        binding.pill = pill
        binding.transitionId = "${pill.id}"
        binding.pillDescription.text = getFormattedDescription(pill, binding.root.context)
        setCardConfirm(pill)
        binding.executePendingBindings()
    }

    private fun setCardConfirm(pill: Pill) {
        pill.getCloseReminder()?.let {
            binding.textQuestionTake.text = binding.root.context.resources.getString(
                R.string.pill_taken_question,
                it.timeString
            )
        } ?: run {
            binding.cardConfirmTake.visibility = View.GONE
        }
    }

    private fun getFormattedDescription(pill: Pill, context: Context): CharSequence {
        return if (pill.description.isNullOrBlank()) {
            pill.getRemindersString(context)
        } else {
            var oneLineDesc = pill.description!!.split("\n")[0]
            if (pill.description!!.contains("\n")) {
                oneLineDesc += "…"
            }
            "${oneLineDesc}\n${pill.getRemindersString(context)}"
        }
    }
}