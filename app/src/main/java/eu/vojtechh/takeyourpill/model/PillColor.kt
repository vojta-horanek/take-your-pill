package eu.vojtechh.takeyourpill.model

import android.content.Context
import androidx.core.content.ContextCompat
import eu.vojtechh.takeyourpill.R

class PillColor(
    val resource: Int
) {
    fun getDrawable(context: Context) = ContextCompat.getDrawable(context, resource)
    fun getAllColorsList() = listOf(
        R.drawable.dot_blue,
        R.drawable.dot_dark_blue,
        R.drawable.dot_green,
        R.drawable.dot_orange,
        R.drawable.dot_red,
        R.drawable.dot_teal,
        R.drawable.dot_yellow
    )
}