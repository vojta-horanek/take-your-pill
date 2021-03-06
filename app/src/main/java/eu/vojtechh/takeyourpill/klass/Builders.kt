package eu.vojtechh.takeyourpill.klass

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.shawnlin.numberpicker.NumberPicker
import eu.vojtechh.takeyourpill.R

object Builders {
    fun getTimePicker(context: Context, hour: Int = 8, minute: Int = 0): MaterialTimePicker {
        val format =
            if (DateFormat.is24HourFormat(context))
                TimeFormat.CLOCK_24H
            else
                TimeFormat.CLOCK_12H

        return MaterialTimePicker.Builder()
            .setTimeFormat(format)
            .setHour(hour)
            .setMinute(minute)
            .build()
    }

    fun getAmountPickerDialog(
        context: Context,
        root: ViewGroup,
        amount: String,
        callback: (String) -> Unit
    ): AlertDialog {
        var newAmount = amount
        val view = LayoutInflater.from(context).inflate(
            R.layout.dialog_change_amount,
            root, false
        )

        val dialog = MaterialAlertDialogBuilder(context).apply {
            setView(view)
            setTitle(R.string.change_amount)
            setMessage(context.getString(R.string.change_amount_format, newAmount))
            setPositiveButton(android.R.string.ok) { dialog, _ ->
                callback(newAmount)
                dialog.dismiss()
            }
            setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        }.create()
        val numberPickerAmount = view.findViewById<NumberPicker>(R.id.numberPickerAmount)

        numberPickerAmount.minValue = 1
        numberPickerAmount.maxValue = NumberPickerHelper.getDisplayValues().size
        numberPickerAmount.displayedValues = NumberPickerHelper.getDisplayValues().toTypedArray()
        numberPickerAmount.value = NumberPickerHelper.convertToPosition(newAmount)
        numberPickerAmount.setOnValueChangedListener { _, _, value ->
            newAmount = NumberPickerHelper.convertToString(value)
            dialog.setMessage(context.getString(R.string.change_amount_format, newAmount))
        }

        return dialog
    }
}