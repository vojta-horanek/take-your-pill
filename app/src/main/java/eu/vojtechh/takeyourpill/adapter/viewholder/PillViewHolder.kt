package eu.vojtechh.takeyourpill.adapter.viewholder

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.adapter.AppRecyclerAdapter
import eu.vojtechh.takeyourpill.databinding.ItemPillBinding
import eu.vojtechh.takeyourpill.model.Pill


class PillViewHolder(
    private val binding: ItemPillBinding,
    private val listener: AppRecyclerAdapter.ItemListener
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
            binding.textQuestionTake.text = binding.root.context.getString(
                    R.string.pill_taken_question,
                    it.amount,
                    it.timeString
            )

            binding.nextReminder = it
            binding.buttonTaken.setOnClickListener { v ->
                listener.onPillConfirmClicked(v, it)
                hideConfirmCard()
            }
        } ?: run {
            binding.cardConfirmTake.visibility = View.GONE
        }
    }

    private fun hideConfirmCard() {
        binding.cardConfirmTake.isVisible = false
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