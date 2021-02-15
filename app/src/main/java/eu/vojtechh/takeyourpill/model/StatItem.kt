package eu.vojtechh.takeyourpill.model

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import eu.vojtechh.takeyourpill.R

data class StatItem(
        val id: Long,
        val name: String,
        val reminded: Int,
        val confirmed: Int,
        val missed: Int,
        val color: Int? = null
) {

    fun getNameText(context: Context) = context.getString(R.string.stat_name, name)
    fun getRemindedText(context: Context) = context.getString(R.string.stat_reminded, reminded)
    fun getConfirmedText(context: Context) = context.getString(R.string.stat_confirmed, confirmed)
    fun getMissedText(context: Context) = context.getString(R.string.stat_missed, missed)

    object DiffCallback : DiffUtil.ItemCallback<StatItem>() {
        override fun areItemsTheSame(oldItem: StatItem, newItem: StatItem) =
                oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: StatItem, newItem: StatItem) =
                oldItem.reminded == newItem.reminded
    }
}