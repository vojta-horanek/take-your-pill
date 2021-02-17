package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.adapter.AppRecyclerAdapter
import eu.vojtechh.takeyourpill.databinding.FragmentHistoryOverviewBinding
import eu.vojtechh.takeyourpill.klass.tryIgnore
import eu.vojtechh.takeyourpill.klass.viewBinding
import eu.vojtechh.takeyourpill.model.BaseModel
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.viewmodel.history.HistoryOverviewViewModel

@AndroidEntryPoint
class HistoryOverviewFragment : Fragment(R.layout.fragment_history_overview),
        AppRecyclerAdapter.ItemListener {

    private val model: HistoryOverviewViewModel by viewModels()

    private val binding by viewBinding(FragmentHistoryOverviewBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appAdapter = AppRecyclerAdapter(
                this,
                null,
                getString(R.string.no_history),
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_fab_history)
        )

        binding.recyclerHistory.adapter = appAdapter

        // TODO Add all pills item
        model.getStatsData().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                appAdapter.submitList(it)
            }.onFailure {
                tryIgnore { (requireParentFragment() as HistoryFragment).disableTabs() }
                appAdapter.submitList(listOf()) // Submit an empty list to show the adapter
            }
        }

    }

    override fun onItemClicked(view: View, item: BaseModel) {
        if (item is Pill) {
            val directions = HistoryFragmentDirections.actionHistoryToFragmentHistoryView(item.id)
            findNavController().navigate(directions)
        }
    }
}