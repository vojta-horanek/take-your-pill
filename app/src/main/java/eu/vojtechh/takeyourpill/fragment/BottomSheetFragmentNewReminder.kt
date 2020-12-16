package eu.vojtechh.takeyourpill.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.format.DateFormat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.FragmentNewReminderBinding
import eu.vojtechh.takeyourpill.model.Reminder
import java.util.*


class BottomSheetFragmentNewReminder :
    BottomSheetDialogFragment() {
    private var editing = false
    private lateinit var snackbar: Snackbar
    private lateinit var binding: FragmentNewReminderBinding

    private var listener: ConfirmListener? = null

    private lateinit var reminder: Reminder

    fun setListener(listener: ConfirmListener): BottomSheetFragmentNewReminder {
        this.listener = listener
        return this
    }

    fun setReminder(reminder: Reminder): BottomSheetFragmentNewReminder {
        this.reminder = reminder
        return this
    }

    fun setEditing(editing: Boolean): BottomSheetFragmentNewReminder {
        this.editing = editing
        return this
    }

    fun showError(msg: String) {
        snackbar.apply { setText(msg) }.show()
        val redColor = resources.getColor(R.color.colorRed, requireContext().theme)
        binding.textTime.apply {
            setTextColor(redColor)
            TextViewCompat.setCompoundDrawableTintList(this, ColorStateList.valueOf(redColor))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewReminderBinding.inflate(inflater, container, false)

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
        val format =
            if (DateFormat.is24HourFormat(requireContext()))
                TimeFormat.CLOCK_24H
            else
                TimeFormat.CLOCK_12H

        val materialTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(format)
            .setHour(reminder.hour)
            .setMinute(reminder.minute)
            .build()

        materialTimePicker.addOnPositiveButtonClickListener {
            val error = reminder.hour == materialTimePicker.hour &&
                    reminder.minute == materialTimePicker.minute &&
                    binding.textTime.textColors != binding.textConfirm.textColors
            reminder.calendar.set(Calendar.HOUR_OF_DAY, materialTimePicker.hour)
            reminder.calendar.set(Calendar.MINUTE, materialTimePicker.minute)
            setTexts(error)
        }

        materialTimePicker.show(childFragmentManager, "time_picker")
    }

    private fun setTexts(error: Boolean = false) {
        binding.run {
            if (!error) {
                val normalColor = binding.textConfirm.textColors
                val typedValue = TypedValue()
                requireContext().theme.resolveAttribute(
                    R.attr.colorControlNormal,
                    typedValue,
                    true
                );
                val color = ContextCompat.getColor(requireContext(), typedValue.resourceId)
                binding.textTime.apply {
                    setTextColor(normalColor)
                    TextViewCompat.setCompoundDrawableTintList(this, ColorStateList.valueOf(color))
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