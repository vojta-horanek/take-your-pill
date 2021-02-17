package eu.vojtechh.takeyourpill.klass

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources.Theme
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import androidx.core.os.ConfigurationCompat
import androidx.core.widget.NestedScrollView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.textfield.TextInputLayout
import java.text.DateFormat
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

fun PopupMenu.forcePopUpMenuToShowIcons() {
    try {
        val method = menu.javaClass.getDeclaredMethod(
                "setOptionalIconsVisible",
                Boolean::class.javaPrimitiveType
        )
        method.isAccessible = true
        method.invoke(menu, true)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun RecyclerView.disableAnimations() {
    (itemAnimator as SimpleItemAnimator).apply {
        changeDuration = 0
        removeDuration = 0
        addDuration = 0
        moveDuration = 0
    }
}

fun Context.getAttr(res: Int): Int {
    val typedValue = TypedValue()
    val theme: Theme = this.theme
    theme.resolveAttribute(res, typedValue, true)
    return typedValue.data
}

fun Any?.isNull() = this == null

fun View.setVerticalBias(bias: Float) {
    val params = layoutParams
    if (params is ConstraintLayout.LayoutParams) {
        params.verticalBias = bias
        layoutParams = params
    }
}

fun TextView.setDateText(date: Date, pattern: String = "dd. MM.") {
    val primaryLocale = ConfigurationCompat.getLocales(context.resources.configuration).get(0)
    val dateFormat = SimpleDateFormat(pattern, primaryLocale)
    text = dateFormat.format(date)
}

fun TextView.setTimeText(date: Date) {
    text = DateFormat.getTimeInstance(DateFormat.SHORT).format(date)
}

// Inline functions
inline fun tryIgnore(action: () -> Unit) {
    try {
        action()
    } catch (t: Throwable) {
    }
}