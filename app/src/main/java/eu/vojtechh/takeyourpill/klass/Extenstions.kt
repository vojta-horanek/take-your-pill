package eu.vojtechh.takeyourpill.klass

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.res.use
import androidx.core.widget.NestedScrollView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

/**
 * Retrieve a color from the current [android.content.res.Resources.Theme].
 */
@ColorInt
@SuppressLint("Recycle")
fun Context.themeColor(
    @AttrRes themeAttrId: Int
): Int {
    return obtainStyledAttributes(
        intArrayOf(themeAttrId)
    ).use {
        it.getColor(0, Color.MAGENTA)
    }
}

fun EditText.getNumber() = this.text.toString().toInt()
fun EditText.getString() = this.text.toString()

fun TextInputLayout.showError(message: String?) {
    isErrorEnabled = message != null
    error = message
}

fun TextView.setDrawableTint(color: Int) {
    TextViewCompat.setCompoundDrawableTintList(this, ColorStateList.valueOf(color))
}

fun NestedScrollView.scrollToBottom() {
    smoothScrollBy(0, this.getChildAt(0).height)
}

fun Long.getDateTimeString(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return SimpleDateFormat.getDateTimeInstance().format(calendar.time)
}

fun Calendar.getDateTimeString(): String {
    return SimpleDateFormat.getDateTimeInstance().format(this.time)
}

fun Long.getTimeString(): String {
    return (this / 60L / 1000L).toString()
}

fun <T, VH : RecyclerView.ViewHolder> ListAdapter<T, VH>.getItemOrNull(position: Int): T? {
    return this.currentList.elementAtOrNull(position)
}

var Calendar.hour: Int
    get() = this.get(Calendar.HOUR_OF_DAY)
    set(value) {
        this.set(Calendar.HOUR_OF_DAY, value)
    }

var Calendar.minute: Int
    get() = this.get(Calendar.MINUTE)
    set(value) {
        this.set(Calendar.MINUTE, value)
    }

fun Calendar.addDay(amount: Int): Calendar {
    this.add(Calendar.DAY_OF_YEAR, amount)
    return this
}