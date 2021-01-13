package eu.vojtechh.takeyourpill.klass

import android.content.Context
import android.text.format.DateFormat
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

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
}