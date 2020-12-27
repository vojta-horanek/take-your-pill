package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.adapter.AppRecyclerAdapter
import eu.vojtechh.takeyourpill.databinding.FragmentHistoryBinding
import eu.vojtechh.takeyourpill.klass.viewBinding
import eu.vojtechh.takeyourpill.model.GeneralRecyclerItem
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.viewmodel.HistoryViewModel

@AndroidEntryPoint
class HistoryFragment : Fragment(R.layout.fragment_history),
    AppRecyclerAdapter.ItemListener {

    private val model: HistoryViewModel by viewModels()
    private val view by viewBinding(FragmentHistoryBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        val appAdapter = AppRecyclerAdapter(this, getString(R.string.history))

        view.recyclerHome.adapter = appAdapter

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