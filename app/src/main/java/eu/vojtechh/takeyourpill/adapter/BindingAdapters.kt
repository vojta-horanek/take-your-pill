package eu.vojtechh.takeyourpill.adapter

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.View
import androidx.databinding.BindingAdapter

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("background_color_shaped")
    fun setBackgroundColorShaped(view: View, color: Int) {
        if (VERSION.SDK_INT >= VERSION_CODES.Q) {
            view.background.colorFilter = BlendModeColorFilter(
                color, BlendMode.SRC_ATOP
            )
        } else {
            @Suppress("DEPRECATION")
            view.background.setColorFilter(
                color,
                PorterDuff.Mode.SRC_ATOP
            )
        }
    }
}