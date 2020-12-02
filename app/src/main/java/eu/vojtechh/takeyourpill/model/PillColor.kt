package eu.vojtechh.takeyourpill.model

import androidx.recyclerview.widget.DiffUtil

class PillColor(
    val color: String,
    var checked: Boolean = false
) {

    companion object {
        fun default() = PillColor("#1976D2")


        const val colorBlue = "#1976D2"
        const val colorDarkBlue = "#303F9F"
        const val colorTeal = "#00796B"
        const val colorGreen = "#388E3C"
        const val colorYellow = "#FFC107"
        const val colorOrange = "#F57C00"
        const val colorRed = "#D32F2F"


        fun getAllPillColorList() = mutableListOf(
            PillColor(colorBlue, false),
            PillColor(colorDarkBlue, false),
            PillColor(colorTeal, false),
            PillColor(colorGreen, false),
            PillColor(colorYellow, false),
            PillColor(colorOrange, false),
            PillColor(colorRed, false),
        )
    }

    object DiffCallback : DiffUtil.ItemCallback<PillColor>() {
        override fun areItemsTheSame(oldItem: PillColor, newItem: PillColor) =
            oldItem.color == newItem.color

        override fun areContentsTheSame(oldItem: PillColor, newItem: PillColor) =
            oldItem.checked == newItem.checked
    }
}