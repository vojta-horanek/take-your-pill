package eu.vojtechh.takeyourpill.viewmodel.history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.model.History
import eu.vojtechh.takeyourpill.repository.HistoryRepository
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class HistoryChartViewModel @Inject constructor(
        historyRepository: HistoryRepository,
        private val pillRepository: PillRepository
) : ViewModel() {
    val allHistory = historyRepository.getHistory()
    private suspend fun getPill(pillId: Long) = pillRepository.getPillSync(pillId)

    fun getStatsData(history: List<History>, pieChart: PieChart, context: Context) = liveData(Dispatchers.IO) {
        val totalReminded = history.size
        val totalConfirmed = history.count { it.hasBeenConfirmed }
        val totalMissed = totalReminded - totalConfirmed

        val colorsAll = mutableListOf<Int>()
        val colorsMissed = mutableListOf<Int>()
        val colorsConfirmed = mutableListOf(
                context.getColor(R.color.colorGreen),
                context.getColor(R.color.colorRed)
        )

        val allEntries: ArrayList<PieEntry> = ArrayList()
        val missedEntries: ArrayList<PieEntry> = ArrayList()
        val confirmedEntries: ArrayList<PieEntry> = arrayListOf(
                PieEntry(totalConfirmed.toFloat(), context.getString(R.string.confirmed)),
                PieEntry(totalMissed.toFloat(), context.getString(R.string.missed))
        )

        val pillsHistory = history.groupBy { it.pillId }.values

        pillsHistory.forEach { pillHistory ->

            val pill = getPill(pillHistory.first().pillId)

            colorsAll.add(pill.colorResource(context))
            allEntries.add(PieEntry(pillHistory.size.toFloat(), pill.name))

            val pillReminded = pillHistory.size
            val pillConfirmed = pillHistory.count { it.hasBeenConfirmed }
            val pillMissed = pillReminded - pillConfirmed

            if (pillMissed > 0) {
                colorsMissed.add(pill.color.getColor(context))
                missedEntries.add(PieEntry(pillMissed.toFloat(), pill.name))
            }

        }

        val dataSetAll = getDataSet(allEntries, colorsAll, context)
        val dataSetMissed = getDataSet(missedEntries, colorsMissed, context)
        val dataSetConfirmed = getDataSet(confirmedEntries, colorsConfirmed, context)

        val dataAll = getData(dataSetAll, pieChart)
        val dataMissed = getData(dataSetMissed, pieChart)
        val dataConfirmed = getData(dataSetConfirmed, pieChart)

        emit(listOf(dataAll, dataMissed, dataConfirmed))
    }

    private fun getData(
            pieDataSet: PieDataSet,
            pieChart: PieChart
    ) =
            PieData(pieDataSet).apply {
                setDrawValues(true)
                setValueFormatter(PercentFormatter(pieChart))
            }

    private fun getDataSet(
            entries: ArrayList<PieEntry>,
            colors: MutableList<Int>,
            context: Context
    ) = PieDataSet(entries, "").apply {
        this.colors = colors
        valueTextSize = 12f
        valueTextColor = context.getColor(android.R.color.white)
        sliceSpace = 4f
    }

}
