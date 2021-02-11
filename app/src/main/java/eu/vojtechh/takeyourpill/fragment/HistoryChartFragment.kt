package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.animation.Easing
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.FragmentHistoryChartBinding
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
                    legend.isEnabled = false
                    setUsePercentValues(true)
                    dragDecelerationFrictionCoef = 0.8f
                    animateY(1400, Easing.EaseInOutQuad)
                    holeRadius = 25f
                    transparentCircleRadius = 30f
                }
            }
        }

        model.run {

            allHistory.observe(viewLifecycleOwner) {
                model.computeStatsData(it, requireContext(), binding.pieChartAll)
            }

            pieDataAll.observe(viewLifecycleOwner) {
                binding.pieChartAll.data = it
                binding.pieChartAll.invalidate()
            }

            pieDataMissed.observe(viewLifecycleOwner) {
                binding.pieChartMissed.data = it
                binding.pieChartMissed.invalidate()
            }
        }
    }

}