package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.animation.Easing
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.FragmentHistoryChartBinding
import eu.vojtechh.takeyourpill.klass.getAttr
import eu.vojtechh.takeyourpill.klass.viewBinding
import eu.vojtechh.takeyourpill.viewmodel.history.HistoryChartViewModel


@AndroidEntryPoint
class HistoryChartFragment : Fragment(R.layout.fragment_history_chart) {

    private val model: HistoryChartViewModel by viewModels()

    private val binding by viewBinding(FragmentHistoryChartBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            listOf(pieChartAll, pieChartMissed).forEach {
                it.apply {
                    description.isEnabled = false
                    legend.textColor = requireContext().getAttr(R.attr.colorOnSurface)
                    legend.isWordWrapEnabled = true
                    legend.textSize = 12f
                    setUsePercentValues(true)
                    dragDecelerationFrictionCoef = 0.8f
                    animateY(1400, Easing.EaseInOutQuad)
                    holeRadius = 25f
                    transparentCircleRadius = 30f
                    setDrawEntryLabels(false)
                }
            }
        }

        model.run {

            allHistory.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    model.computeStatsData(it, requireContext(), binding.pieChartAll)
                    binding.cardCharts.isVisible = true
                }
            }

            pieDataAll.observe(viewLifecycleOwner) {
                binding.pieChartAll.data = it
                binding.pieChartAll.invalidate()
            }

            pieDataMissed.observe(viewLifecycleOwner) {
                binding.pieChartMissed.data = it
                binding.pieChartMissed.invalidate()
                binding.layoutChartContent.isVisible = true
                binding.progressCharts.setVisibilityAfterHide(View.INVISIBLE)
                binding.progressCharts.hide()
            }
        }
    }

}