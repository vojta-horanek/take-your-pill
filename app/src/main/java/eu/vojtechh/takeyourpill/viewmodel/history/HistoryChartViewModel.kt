package eu.vojtechh.takeyourpill.viewmodel.history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.repository.HistoryRepository
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HistoryChartViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val pillRepository: PillRepository
) : ViewModel() {

    fun getStatsData(applicationContext: Context) =
        historyRepository.getHistoryFlow().map { history ->

            if (history.isEmpty()) {
                return@map null
            }

            val totalConfirmed = history.count { it.hasBeenConfirmed }
            val totalMissed = history.size - totalConfirmed

            val colorsAll = mutableListOf<Int>()
            val colorsMissed = mutableListOf<Int>()
            val colorsConfirmed = mutableListOf(
                applicationContext.getColor(R.color.colorGreen),
                applicationContext.getColor(R.color.colorRed)
            )

            val allEntries = ArrayList<PieEntry>()
            val missedEntries = ArrayList<PieEntry>()
            val confirmedEntries: ArrayList<PieEntry> = arrayListOf(
                PieEntry(
                    totalConfirmed.toFloat(),
                    applicationContext.getString(R.string.confirmed)
                ),
                PieEntry(totalMissed.toFloat(), applicationContext.getString(R.string.missed))
            )

            val pillsHistory = history.groupBy { it.pillId }.values

            pillsHistory.forEach { pillHistory ->

                val pill = pillRepository.getPill(pillHistory.first().pillId)

                colorsAll.add(pill.colorResource(applicationContext))
                allEntries.add(PieEntry(pillHistory.size.toFloat(), pill.name))

                val pillMissed = pillHistory.count { !it.hasBeenConfirmed }

                if (pillMissed > 0) {
                    colorsMissed.add(pill.color.getColor(applicationContext))
                    missedEntries.add(PieEntry(pillMissed.toFloat(), pill.name))
                }
            }

            val dataSetAll = getDataSet(allEntries, colorsAll, applicationContext)
            val dataSetMissed = getDataSet(missedEntries, colorsMissed, applicationContext)
            val dataSetConfirmed = getDataSet(confirmedEntries, colorsConfirmed, applicationContext)

            val dataAll = getData(dataSetAll)
            val dataMissed = getData(dataSetMissed)
            val dataConfirmed = getData(dataSetConfirmed)

            return@map (listOf(dataAll, dataMissed, dataConfirmed))
        }.asLiveData()

    private fun getData(
        pieDataSet: PieDataSet,
    ) =
        PieData(pieDataSet).apply {
            setDrawValues(true)
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
