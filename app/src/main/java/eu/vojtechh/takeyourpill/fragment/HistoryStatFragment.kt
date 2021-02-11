package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.adapter.StatItemAdapter
import eu.vojtechh.takeyourpill.databinding.FragmentHistoryStatsBinding
import eu.vojtechh.takeyourpill.klass.viewBinding
import eu.vojtechh.takeyourpill.viewmodel.history.HistoryStatViewModel


@AndroidEntryPoint
class HistoryStatFragment : Fragment(R.layout.fragment_history_stats) {

    private val model: HistoryStatViewModel by viewModels()

    private val binding by viewBinding(FragmentHistoryStatsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val statAdapter = StatItemAdapter()

        binding.run {
            recyclerStats.adapter = statAdapter
        }

        model.run {

            allHistory.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    model.computeStatsData(it, requireContext())
                    binding.cardStats.isVisible = true
                }
            }

            stats.observe(viewLifecycleOwner) {
                statAdapter.submitList(it)
                binding.progressStats.setVisibilityAfterHide(View.INVISIBLE)
                binding.progressStats.hide()
            }
        }
    }

}