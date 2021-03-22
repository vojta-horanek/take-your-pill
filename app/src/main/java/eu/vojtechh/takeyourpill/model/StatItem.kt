package eu.vojtechh.takeyourpill.model

import android.content.Context
import eu.vojtechh.takeyourpill.R

data class StatItem(
        val pillId: Long?,
        val reminded: Int,
        val confirmed: Int,
        val missed: Int
) {

    private fun getRemindedText(context: Context) =
        context.getString(R.string.stat_reminded, reminded)

    private fun getConfirmedText(context: Context) =
        context.getString(R.string.stat_confirmed, confirmed)

    private fun getMissedText(context: Context) = context.getString(R.string.stat_missed, missed)
    fun getSummaryText(context: Context) =
        listOf(
            getRemindedText(context),
            getConfirmedText(context),
            getMissedText(context)
        ).joinToString(separator = System.lineSeparator())

    val hasStats
        get() = reminded != 0
}