package eu.vojtechh.takeyourpill.model

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import eu.vojtechh.takeyourpill.R

class PillColor(
    val resource: Int,
    var isChecked: Boolean = false
) {
    fun getColor(context: Context) = ContextCompat.getColor(context, resource)

    companion object {
        fun default() = PillColor(R.color.colorBlue)
        fun teal() = PillColor(R.color.colorTeal)
        fun red() = PillColor(R.color.colorRed)

        const val BLUE = 1
        const val DARK_BLUE = 2
        const val GREEN = 3
        const val ORANGE = 4
        const val RED = 5
        const val TEAL = 6
        const val YELLOW = 7

        fun getAllPillColorList() = mutableListOf(
            PillColor(R.color.colorBlue, false),
            PillColor(R.color.colorDarkBlue, false),
            PillColor(R.color.colorTeal, false),
            PillColor(R.color.colorGreen, false),
            PillColor(R.color.colorYellow, false),
            PillColor(R.color.colorOrange, false),
            PillColor(R.color.colorRed, false),
        )
    }

    object DiffCallback : DiffUtil.ItemCallback<PillColor>() {
        override fun areItemsTheSame(oldItem: PillColor, newItem: PillColor) =
            oldItem.resource == newItem.resource

        override fun areContentsTheSame(oldItem: PillColor, newItem: PillColor) =
            oldItem.isChecked == newItem.isChecked
    }
}