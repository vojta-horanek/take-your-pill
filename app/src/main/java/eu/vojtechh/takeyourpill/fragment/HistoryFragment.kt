package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.adapter.AppRecyclerAdapter
import eu.vojtechh.takeyourpill.databinding.FragmentHistoryBinding
import eu.vojtechh.takeyourpill.model.GeneralRecyclerItem
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.viewmodel.HistoryViewModel

@AndroidEntryPoint
class HistoryFragment : Fragment(), AppRecyclerAdapter.ItemListener {

    private val model: HistoryViewModel by viewModels()

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appAdapter = AppRecyclerAdapter(
            this,
            getString(R.string.history),
            getString(R.string.history),
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_fab_history)
        )

        binding.recyclerHome.adapter = appAdapter

        model.allPills.observe(viewLifecycleOwner, {
            it.map { pill -> pill.itemType = GeneralRecyclerItem.ItemTypes.HISTORY }
            appAdapter.submitList(it)
        })
    }

    override fun onItemClicked(view: View, item: GeneralRecyclerItem) {
        if (item is Pill) {
            val directions = HistoryFragmentDirections.actionHistoryToFragmentHistoryView(item.id)
            findNavController().navigate(directions)
        }
    }
}