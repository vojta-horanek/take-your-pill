package eu.vojtechh.takeyourpill.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.model.PillColor
import java.io.ByteArrayOutputStream
import java.util.*

class Converters {
    @TypeConverter
    fun toBitmap(bytes: ByteArray?) =
        bytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }


    @TypeConverter
    fun fromBitmap(bmp: Bitmap?) =
        bmp?.let {
            val outputStream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.toByteArray()
        }

    @TypeConverter
    fun fromPillColor(pillColor: PillColor) =
        when (pillColor.resource) {
            R.color.colorBlue -> PillColor.BLUE
            R.color.colorDarkBlue -> PillColor.DARK_BLUE
            R.color.colorGreen -> PillColor.GREEN
            R.color.colorOrange -> PillColor.ORANGE
            R.color.colorRed -> PillColor.RED
            R.color.colorTeal -> PillColor.TEAL
            R.color.colorYellow -> PillColor.YELLOW
            else -> PillColor.BLUE
        }

    @TypeConverter
    fun toPillColor(pillColor: Int) =
        PillColor(
            when (pillColor) {
                PillColor.BLUE -> R.color.colorBlue
                PillColor.DARK_BLUE -> R.color.colorDarkBlue
                PillColor.GREEN -> R.color.colorGreen
                PillColor.ORANGE -> R.color.colorOrange
                PillColor.RED -> R.color.colorRed
                PillColor.TEAL -> R.color.colorTeal
                PillColor.YELLOW -> R.color.colorYellow
                else -> R.color.colorBlue
            }
        )

    @TypeConverter
    fun toCalendar(millis: Long?) = millis?.let {
        Calendar.getInstance().apply {
            timeInMillis = it
        }
    }

    @TypeConverter
    fun fromCalendar(calendar: Calendar?) = calendar?.timeInMillis
}