package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.FragmentHistoryChartBinding
import eu.vojtechh.takeyourpill.klass.viewBinding
import eu.vojtechh.takeyourpill.viewmodel.HistoryChartViewModel


@AndroidEntryPoint
class HistoryChartFragment : Fragment(R.layout.fragment_history_chart) {

    private val model: HistoryChartViewModel by viewModels()

    private val binding by viewBinding(FragmentHistoryChartBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            pieChart.description.isEnabled = false
            pieChart.legend.isEnabled = false
            pieChart.setUsePercentValues(true)
        }

        model.run {

            allHistory.observe(viewLifecycleOwner) {
                model.computeStatsData(it, requireContext(), binding.pieChart)
            }

            statsText.observe(viewLifecycleOwner) {
                binding.statsText.text = it
            }

            pieData.observe(viewLifecycleOwner) {
                binding.pieChart.data = it
                binding.pieChart.invalidate()
            }
        }
    }

}