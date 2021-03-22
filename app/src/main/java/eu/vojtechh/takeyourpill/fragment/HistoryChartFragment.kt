package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.formatter.PercentFormatter
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.FragmentHistoryChartBinding
import eu.vojtechh.takeyourpill.klass.applicationContext
import eu.vojtechh.takeyourpill.klass.getAttrColor
import eu.vojtechh.takeyourpill.viewmodel.MainViewModel
import eu.vojtechh.takeyourpill.viewmodel.history.HistoryChartViewModel


@AndroidEntryPoint
class HistoryChartFragment : Fragment(R.layout.fragment_history_chart) {

    private val model: HistoryChartViewModel by viewModels()
    private val mainModel: MainViewModel by activityViewModels()

    private val binding by viewBinding(FragmentHistoryChartBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            listOf(pieChartAll, pieChartMissed, pieChartAllConfirmed).forEach { setupPieChart(it) }
        }

        model.run {
            model.getStatsData(applicationContext)
                .observe(viewLifecycleOwner) { data ->
                    data?.let {
                        processData(it)
                    }
                }
        }

        mainModel.shouldScrollUp.observe(viewLifecycleOwner) {
            if (isVisible) binding.chartsScrollView.smoothScrollTo(0, 0)
        }
    }

    private fun setupPieChart(pieChart: PieChart) {
        pieChart.apply {
            description.isEnabled = false
            setUsePercentValues(true)
            dragDecelerationFrictionCoef = 0.8f
            animateY(1400, Easing.EaseInOutQuad)
            holeRadius = 25f
            isDrawHoleEnabled = false
            transparentCircleRadius = 30f
            setDrawEntryLabels(true)
        }
        pieChart.legend.apply {
            textColor = requireContext().getAttrColor(R.attr.colorOnSurface)
            isWordWrapEnabled = true
            textSize = 12f
            isEnabled = false
        }
    }

    private fun processData(data: List<PieData>) {
        binding.run {
            listOf(
                Pair(pieChartAll, cardChartAll),
                Pair(pieChartMissed, cardChartMissed),
                Pair(pieChartAllConfirmed, cardChartAllMissed)
            ).forEachIndexed { index, chart ->
                if (data[index].entryCount == 0) {
                    chart.first.isVisible = false
                    chart.second.isVisible = false
                } else {
                    chart.first.isVisible = true
                    chart.second.isVisible = true
                }
                data[index].setValueFormatter(PercentFormatter(chart.first))
                chart.first.data = data[index]
                chart.first.invalidate()
            }
        }
    }

}