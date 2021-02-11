package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.FragmentHistoryChartBinding
import eu.vojtechh.takeyourpill.klass.ChartValueFormatter
import eu.vojtechh.takeyourpill.klass.viewBinding
import eu.vojtechh.takeyourpill.viewmodel.HistoryChartViewModel
import java.util.*


@AndroidEntryPoint
class HistoryChartFragment : Fragment(R.layout.fragment_history_chart) {

    private val model: HistoryChartViewModel by viewModels()

    private val binding by viewBinding(FragmentHistoryChartBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.allHistory.observe(viewLifecycleOwner, {

            val days = it.groupBy { history -> history.reminded.get(Calendar.DAY_OF_YEAR) }.values
            for (day in days) { // Iterate day by day
                val historiesOfPill = day.groupBy { history -> history.pillId }.values
                model.chartData.add(
                    BarEntry(
                        day.first().reminded.get(Calendar.DAY_OF_YEAR).toFloat(),
                        historiesOfPill.map { historyOfPill -> historyOfPill.size.toFloat() }
                            .toFloatArray()
                    ))
            }
            val dataSet = BarDataSet(model.chartData, "Pill Taking History").apply {
                stackLabels = listOf("", "").toTypedArray()
                setColors()
            }

            val typedValue = TypedValue()
            val theme = requireContext().theme
            theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true)
            @ColorInt val color = typedValue.data
            val barData = BarData(dataSet).apply {
                setValueTextColor(color)
                setValueFormatter(ChartValueFormatter())
            }
            binding.barChart.data = barData
            binding.barChart.invalidate()
        })

    }

}