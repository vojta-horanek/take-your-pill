package eu.vojtechh.takeyourpill.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.DialogNewReminderBinding
import eu.vojtechh.takeyourpill.klass.Builders
import eu.vojtechh.takeyourpill.klass.getAttrColor
import eu.vojtechh.takeyourpill.klass.onClick
import eu.vojtechh.takeyourpill.klass.setDrawableTint
import eu.vojtechh.takeyourpill.model.PillColor
import eu.vojtechh.takeyourpill.model.Reminder
import java.util.*

class ReminderDialog : RoundedDialogFragment() {
    private lateinit var binding: DialogNewReminderBinding

    private lateinit var snackbar: Snackbar

    var confirmListener: (Reminder, Boolean) -> Unit = { _, _ -> }
    lateinit var reminder: Reminder
    var isEditing = false
    var accentColor = PillColor.default()

    fun onConfirm(action: (Reminder, Boolean) -> Unit) {
        confirmListener = action
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if (!this::reminder.isInitialized) {
            throw IllegalStateException("A reminder must be set!")
        }

        binding = DialogNewReminderBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTexts()
        binding.run {
            snackbar = Snackbar.make(coordinatorNewReminder, "", Snackbar.LENGTH_SHORT)

            textTime.onClick {
                snackbar.dismiss()
                showTimeDialog()
            }

            textAmount.onClick { showAmountDialog() }
            textConfirm.onClick { confirmListener(reminder, isEditing) }

        }

    }

    private fun showAmountDialog() =
        Builders.getAmountPickerDialog(
            requireContext(),
            binding.root as ViewGroup,
            reminder.amount
        ) {
            reminder.amount = it
            setTexts()
        }.show()


    private fun showTimeDialog() {
        val timePicker = Builders.getTimePicker(requireContext(), reminder.hour, reminder.minute)
        timePicker.addOnPositiveButtonClickListener {
            onTimePickerConfirmed(timePicker.hour, timePicker.minute)
        }
        timePicker.show(childFragmentManager, "time_picker")
    }

    private fun onTimePickerConfirmed(hour: Int, minute: Int) {
        val error = reminder.hour == hour &&
                reminder.minute == minute &&
                binding.textTime.textColors != binding.textConfirm.textColors
        reminder.time.set(Calendar.HOUR_OF_DAY, hour)
        reminder.time.set(Calendar.MINUTE, minute)
        setTexts(error)
    }

    private fun setTexts(error: Boolean = false) {
        binding.run {
            if (!error) {
                textTime.apply {
                    setTextColor(textConfirm.textColors)
                    setDrawableTint(requireContext().getAttrColor(R.attr.colorControlNormal))
                }
            }
            textAmount.text = getString(R.string.set_amount_format, reminder.amount)
            textTime.text =
                getString(R.string.set_time_format, reminder.getTimeString(requireContext()))

        }
    }

    fun showError(msg: String) {
        snackbar.apply { setText(msg) }.show()
        val redColor = resources.getColor(R.color.colorRed, requireContext().theme)
        binding.textTime.apply {
            setTextColor(redColor)
            setDrawableTint(redColor)
        }
    }
}