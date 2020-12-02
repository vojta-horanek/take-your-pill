package eu.vojtechh.takeyourpill.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    fun fromPillColor(pillColor: PillColor): String {
        return pillColor.color
    }

    @TypeConverter
    fun toPillColor(pillColor: String): PillColor {
        return PillColor(pillColor)
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