package eu.vojtechh.takeyourpill.adapter.viewholder

import android.content.Context
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.adapter.AppRecyclerAdapter
import eu.vojtechh.takeyourpill.databinding.ItemPillBinding
import eu.vojtechh.takeyourpill.klass.DayOfYear
import eu.vojtechh.takeyourpill.model.Pill
import java.util.*


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
        val description = getFormattedDescription(pill, binding.root.context)

        if (description.isBlank()) {
            binding.pillDescription.isVisible = false
        } else {
            binding.pillDescription.text = description
        }

        pill.reminders.sortedBy { rem -> rem.time.time }.forEach { reminder ->
            val chip = Chip(binding.root.context)
            chip.text = reminder.getAmountTimeString(binding.root.context)
            chip.isFocusable = false
            chip.isClickable = false
            chip.foreground = null
            binding.chipsLayout.addView(chip)
        }

        setIntakeOptions(pill, binding.root.context)
        setCardConfirm(pill, binding.root.context)
        binding.executePendingBindings()
    }

    private fun setIntakeOptions(pill: Pill, context: Context) {

        // If last reminder date is null, then this is the first reminder
        pill.lastReminderDate?.let { lastDate ->
            // Only add next cycle if this is the first reminder today
            if (lastDate.DayOfYear != Calendar.getInstance().DayOfYear) {
                pill.options.nextCycleIteration()
            }
        }

        with(pill.options) {
            when {
                isIndefinite() -> {
                    binding.textIntakeTitle.isVisible = false
                    binding.textIntakeOptions.isVisible = false
                    binding.divider.isVisible = false
                }
                isFinite() -> {
                    binding.textIntakeTitle.text = context.getString(R.string.intake_options_x_days)
                    binding.textIntakeOptions.text = context.getString(
                        R.string.intake_options_x_days_text,
                        daysActive,
                        daysActive - todayCycle
                    )
                }
                isCycle() -> {
                    binding.textIntakeTitle.text = context.getString(R.string.cycle)
                    binding.textIntakeOptions.text =
                        context.getString(
                            R.string.intake_options_cycle,
                            daysActive,
                            daysInactive,
                            todayCycle
                        )
                }
            }
        }
    }

    private fun setCardConfirm(pill: Pill, context: Context) {
        pill.getCloseReminder()?.let {
            binding.textQuestionTake.text = binding.root.context.getString(
                R.string.pill_taken_question,
                it.amount,
                it.getTimeString(context)
            )

            binding.nextReminder = it
            binding.buttonTaken.setOnClickListener { v ->
                listener.onPillConfirmClicked(v, it)
                hideConfirmCard()
            }
        } ?: run {
            binding.pillConfirm.isVisible = false
        }
    }

    private fun hideConfirmCard() {
        binding.pillConfirm.isVisible = false
    }

    private fun getFormattedDescription(pill: Pill, context: Context): CharSequence {
        return pill.description?.let { desc ->
            var oneLineDesc = desc.split("\n")[0]
            if (desc.contains("\n")) {
                oneLineDesc += "…"
            }
            oneLineDesc
        } ?: ""
    }
}