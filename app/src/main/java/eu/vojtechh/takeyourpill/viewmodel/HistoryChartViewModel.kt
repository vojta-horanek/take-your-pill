package eu.vojtechh.takeyourpill.viewmodel

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.model.History
import eu.vojtechh.takeyourpill.repository.HistoryRepository
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryChartViewModel @ViewModelInject constructor(
    historyRepository: HistoryRepository,
    private val pillRepository: PillRepository
) : ViewModel() {
    val allHistory = historyRepository.getHistory()
    private suspend fun getPill(pillId: Long) = pillRepository.getPillSync(pillId)

    val statsText = MutableLiveData("")
    val pieData = MutableLiveData<PieData>()

    fun computeStatsData(history: List<History>, context: Context, pieChart: PieChart) =
        viewModelScope.launch(Dispatchers.IO) {
            var statsText = ""
            val totalReminded = history.count()
            val totalConfirmed =
                history.filter { history -> history.hasBeenConfirmed }.count()
            val totalMissed = totalReminded - totalConfirmed
            statsText += "You took $totalConfirmed of $totalReminded in total ($totalMissed missed)\n"

            val pillsHistory = history.groupBy { history -> history.pillId }.values
            val colors = mutableListOf<Int>()
            val pieEntries: ArrayList<PieEntry> = ArrayList()

            pillsHistory.forEach { pillHistory ->
                val pill = getPill(pillHistory.first().pillId)

                colors.add(pill.color.getColor(context))
                pieEntries.add(PieEntry(pillHistory.size.toFloat(), pill.name))

                val pillReminded = pillHistory.count()
                val pillConfirmed =
                    pillHistory.filter { history -> history.hasBeenConfirmed }.count()
                val pillMissed = pillReminded - pillConfirmed
                statsText += "${pill.name}: $pillConfirmed of $pillReminded ($pillMissed missed)\n"
            }

            val pieDataSet = PieDataSet(pieEntries, context.getString(R.string.pill)).apply {
                setColors(colors)
                valueTextSize = 12f
            }

            val pieData = PieData(pieDataSet).apply {
                setDrawValues(true)
                setValueFormatter(PercentFormatter(pieChart))
            }

            this@HistoryChartViewModel.pieData.postValue(pieData)
            this@HistoryChartViewModel.statsText.postValue(statsText)
        }
}
