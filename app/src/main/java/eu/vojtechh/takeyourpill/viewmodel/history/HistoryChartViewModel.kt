package eu.vojtechh.takeyourpill.viewmodel.history

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryChartViewModel @Inject constructor(
    historyRepository: HistoryRepository,
    private val pillRepository: PillRepository
) : ViewModel() {
    val allHistory = historyRepository.getHistory()
    private suspend fun getPill(pillId: Long) = pillRepository.getPillSync(pillId)

    val pieDataAll = MutableLiveData<PieData>()
    val pieDataMissed = MutableLiveData<PieData>()
    val pieDataConfirmed = MutableLiveData<PieData>()

    fun computeStatsData(history: List<History>, context: Context, pieChart: PieChart) =
        viewModelScope.launch(Dispatchers.IO) {

            val totalReminded = history.count()
            val totalConfirmed =
                history.filter { history -> history.hasBeenConfirmed }.count()
            val totalMissed = totalReminded - totalConfirmed

            val pillsHistory = history.groupBy { history -> history.pillId }.values

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

            val pieDataSetAll = getPieDataSet(allEntries, colorsAll, context)

            val pieDataSetMissed =
                getPieDataSet(missedEntries, colorsMissed, context)

            val pieDataSetConfirmed = getPieDataSet(
                confirmedEntries, colorsConfirmed, context
            )

            val pieDataAll = getPieData(pieDataSetAll, pieChart)
            val pieDataMissed = getPieData(pieDataSetMissed, pieChart)
            val pieDataConfirmed = getPieData(pieDataSetConfirmed, pieChart)

            this@HistoryChartViewModel.pieDataAll.postValue(pieDataAll)
            this@HistoryChartViewModel.pieDataConfirmed.postValue(pieDataConfirmed)
            this@HistoryChartViewModel.pieDataMissed.postValue(pieDataMissed)
        }

    private fun getPieData(
        pieDataSet: PieDataSet,
        pieChart: PieChart
    ) =
        PieData(pieDataSet).apply {
            setDrawValues(true)
            setValueFormatter(PercentFormatter(pieChart))
        }

    private fun getPieDataSet(
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
