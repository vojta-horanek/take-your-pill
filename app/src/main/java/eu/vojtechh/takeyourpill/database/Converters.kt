package eu.vojtechh.takeyourpill.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.model.PillColor
import eu.vojtechh.takeyourpill.model.Reminder
import java.io.ByteArrayOutputStream


class Converters {
    @TypeConverter
    fun toBitmap(bytes: ByteArray?): Bitmap? {
        return bytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
    }

    @TypeConverter
    fun fromBitmap(bmp: Bitmap?): ByteArray? {
        if (bmp == null) return null
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun fromPillColor(pillColor: PillColor): Int {
        return when (pillColor.resource) {
            R.color.colorBlue -> PillColor.BLUE
            R.color.colorDarkBlue -> PillColor.DARK_BLUE
            R.color.colorGreen -> PillColor.GREEN
            R.color.colorOrange -> PillColor.ORANGE
            R.color.colorRed -> PillColor.RED
            R.color.colorTeal -> PillColor.TEAL
            R.color.colorYellow -> PillColor.YELLOW
            else -> PillColor.BLUE
        }
    }

    @TypeConverter
    fun toPillColor(pillColor: Int): PillColor {
        return PillColor(
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
    }

    @TypeConverter
    fun jsonToList(listOfCReminder: String): MutableList<Reminder> {
        return Gson().fromJson(
            listOfCReminder,
            object : TypeToken<MutableList<Reminder>>() {}.type
        )
    }

    @TypeConverter
    fun listToJson(listOfString: MutableList<Reminder>): String {
        return Gson().toJson(listOfString)
    }
}