package eu.vojtechh.takeyourpill.klass

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.EditText
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.res.use
import androidx.core.widget.NestedScrollView
import com.google.android.material.textfield.TextInputLayout

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

fun View.setVisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.getVisible() = this.visibility == View.VISIBLE

fun EditText.getNumber() = this.text.toString().toInt()
fun EditText.getString() = this.text.toString()

fun TextInputLayout.showError(message: String?) {
    isErrorEnabled = message != null
    error = message
}

fun NestedScrollView.scrollToBottom() {
    smoothScrollBy(0, this.getChildAt(0).height)
}

fun View.slideUp(duration: Int = 400) {
    // TODO Parent view changes height only after animation finished
    visibility = View.VISIBLE
    val animate = TranslateAnimation(0f, 0f, 0f, -this.height.toFloat())
    animate.duration = duration.toLong()
    animate.fillAfter = true
    animate.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) {
            this@slideUp.visibility = View.GONE
        }

        override fun onAnimationRepeat(animation: Animation?) {}
    })
    this.startAnimation(animate)
}