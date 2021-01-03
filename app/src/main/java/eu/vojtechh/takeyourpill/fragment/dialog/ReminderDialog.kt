package eu.vojtechh.takeyourpill.fragment.dialog

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.DialogNewReminderBinding
import eu.vojtechh.takeyourpill.klass.Builders
import eu.vojtechh.takeyourpill.klass.setDrawableTint
import eu.vojtechh.takeyourpill.model.Reminder
import java.util.*

class ReminderDialog :
    RoundedDialogFragment() {
    private var editing = false
    private lateinit var snackbar: Snackbar
    private lateinit var binding: DialogNewReminderBinding

    private var listener: ConfirmListener? = null

    private lateinit var reminder: Reminder

    fun setListener(listener: ConfirmListener): ReminderDialog {
        this.listener = listener
        return this
    }

    fun setReminder(reminder: Reminder): ReminderDialog {
        this.reminder = reminder
        return this
    }

    fun setEditing(editing: Boolean): ReminderDialog {
        this.editing = editing
        return this
    }

    fun showError(msg: String) {
        snackbar.apply { setText(msg) }.show()
        val redColor = resources.getColor(R.color.colorRed, requireContext().theme)
        binding.textTime.apply {
            setTextColor(redColor)
            setDrawableTint(redColor)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogNewReminderBinding.inflate(inflater, container, false)

        snackbar = Snackbar.make(binding.coordinatorNewReminder, "", Snackbar.LENGTH_SHORT)
        setTexts()
        binding.numberPickerAmount.value = reminder.amount

        binding.textConfirm.setOnClickListener {
            listener?.onNewReminderClicked(reminder, editing)
        }

        binding.numberPickerAmount.setOnValueChangedListener { _, _, value ->
            reminder.amount = value
            setTexts()
        }

        binding.textTime.setOnClickListener {
            snackbar.dismiss()
            showTimeDialog()
        }

        return binding.root
    }

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
                val normalColor = textConfirm.textColors
                val typedValue = TypedValue()
                requireContext().theme.resolveAttribute(
                    R.attr.colorControlNormal,
                    typedValue,
                    true
                )
                val color = ContextCompat.getColor(requireContext(), typedValue.resourceId)
                textTime.apply {
                    setTextColor(normalColor)
                    setDrawableTint(color)
                }
            }
            textAmount.text =
                resources.getQuantityString(
                    R.plurals.set_amount_format,
                    reminder.amount,
                    reminder.amount
                )
            textTime.text = resources.getString(
                R.string.set_time_format,
                reminder.timeString
            )

        }
    }

    interface ConfirmListener {
        fun onNewReminderClicked(reminder: Reminder, editing: Boolean)
    }
}