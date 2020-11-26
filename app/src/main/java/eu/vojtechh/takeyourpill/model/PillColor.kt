package eu.vojtechh.takeyourpill.model

import android.content.Context
import androidx.core.content.ContextCompat
import eu.vojtechh.takeyourpill.R

class PillColor(
    val resource: Int
) {
    fun getColor(context: Context) = ContextCompat.getColor(context, resource)
    fun getAllColorsList() = listOf(
        R.color.colorBlue,
        R.color.colorDarkBlue,
        R.color.colorGreen,
        R.color.colorOrange,
        R.color.colorRed,
        R.color.colorTeal,
        R.color.colorYellow,
    )
}