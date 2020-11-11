package eu.vojtechh.takeyourpill.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import eu.vojtechh.takeyourpill.model.PillColor
import java.io.ByteArrayOutputStream
import java.util.*


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
        return pillColor.resource
    }

    @TypeConverter
    fun toPillColor(pillColor: Int): PillColor {
        return PillColor(pillColor)
    }

    @TypeConverter
    fun jsonToList(listOfCalendar: String): MutableList<Calendar> {
        return Gson().fromJson(
            listOfCalendar,
            object : TypeToken<MutableList<Calendar>>() {}.type
        )
    }

    @TypeConverter
    fun listToJson(listOfString: MutableList<Calendar>): String {
        return Gson().toJson(listOfString)
    }
}