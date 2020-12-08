package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.FragmentNewReminderBinding
import eu.vojtechh.takeyourpill.model.Reminder
import java.text.SimpleDateFormat
import java.util.*


class BottomSheetFragmentNewReminder :
    BottomSheetDialogFragment() {
    private var editing = false
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewReminderBinding.inflate(inflater, container, false)

        setTexts()
        binding.numberPickerAmount.value = reminder.amount

        binding.textConfirm.setOnClickListener {
            listener?.onNewPillConfirmClicked(reminder, editing)
        }

        binding.numberPickerAmount.setOnValueChangedListener { _, _, value ->
            reminder.amount = value
            setTexts()
        }

        binding.textTime.setOnClickListener {
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
            reminder.time.set(Calendar.HOUR_OF_DAY, materialTimePicker.hour)
            reminder.time.set(Calendar.MINUTE, materialTimePicker.minute)
            setTexts()
        }

        materialTimePicker.show(childFragmentManager, "time_picker")
    }

    private fun setTexts() {
        binding.run {
            textAmount.text =
                resources.getQuantityString(
                    R.plurals.set_amount_format,
                    reminder.amount,
                    reminder.amount
                )
            textTime.text = resources.getString(
                R.string.set_time_format,
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(reminder.time.time)
            )

        }
    }

    interface ConfirmListener {
        fun onNewPillConfirmClicked(reminder: Reminder, editing: Boolean)
    }
}