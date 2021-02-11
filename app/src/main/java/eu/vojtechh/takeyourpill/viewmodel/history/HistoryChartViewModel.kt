package eu.vojtechh.takeyourpill.viewmodel.history

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
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

    val pieDataAll = MutableLiveData<PieData>()
    val pieDataMissed = MutableLiveData<PieData>()

    fun computeStatsData(history: List<History>, context: Context, pieChart: PieChart) =
        viewModelScope.launch(Dispatchers.IO) {

            val totalReminded = history.count()
            val totalConfirmed =
                history.filter { history -> history.hasBeenConfirmed }.count()
            val totalMissed = totalReminded - totalConfirmed

            val pillsHistory = history.groupBy { history -> history.pillId }.values
            val colorsAll = mutableListOf<Int>()
            val colorsMissed = mutableListOf<Int>()
            val allEntries: ArrayList<PieEntry> = ArrayList()
            val missedEntries: ArrayList<PieEntry> = ArrayList()

            pillsHistory.forEach { pillHistory ->
                val pill = getPill(pillHistory.first().pillId)

                colorsAll.add(pill.color.getColor(context))
                allEntries.add(PieEntry(pillHistory.size.toFloat(), pill.name))

                val pillReminded = pillHistory.count()
                val pillConfirmed =
                    pillHistory.filter { history -> history.hasBeenConfirmed }.count()
                val pillMissed = pillReminded - pillConfirmed

                if (pillMissed > 0) {
                    colorsMissed.add(pill.color.getColor(context))
                    missedEntries.add(PieEntry(pillMissed.toFloat(), pill.name))
                }

            }

            val pieDataSetAll = PieDataSet(allEntries, "").apply {
                colors = colorsAll
                valueTextSize = 12f
                valueTextColor = context.getColor(android.R.color.white)
                sliceSpace = 4f
            }

            val pieDataSetMissed =
                PieDataSet(missedEntries, "").apply {
                    colors = colorsMissed
                    valueTextSize = 12f
                    valueTextColor = context.getColor(android.R.color.white)
                    sliceSpace = 4f
                }

            val pieDataAll = PieData(pieDataSetAll).apply {
                setDrawValues(true)
                setValueFormatter(PercentFormatter(pieChart))
            }

            val pieDataMissed = PieData(pieDataSetMissed).apply {
                setDrawValues(true)
                setValueFormatter(PercentFormatter(pieChart))
            }

            this@HistoryChartViewModel.pieDataAll.postValue(pieDataAll)
            this@HistoryChartViewModel.pieDataMissed.postValue(pieDataMissed)
        }
}
