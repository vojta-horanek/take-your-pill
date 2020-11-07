package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.adapter.PillsAdapter
import eu.vojtechh.takeyourpill.databinding.HomeFragmentBinding
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.klass.viewBinding
import eu.vojtechh.takeyourpill.viewmodel.HomeViewModel

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.home_fragment) {

    private val model: HomeViewModel by viewModels()
    private val view by viewBinding(HomeFragmentBinding::bind)

    private lateinit var pillsAdapter: PillsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //FIXME Don't animate FAB
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = Constants.ANIMATION_DURATION
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = Constants.ANIMATION_DURATION
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        pillsAdapter = PillsAdapter(requireContext())
        view.recyclerHome.adapter = pillsAdapter

        view.recyclerHome.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    activity?.findViewById<ExtendedFloatingActionButton>(R.id.floatingActionButton)
                        ?.shrink()
                } else {
                    activity?.findViewById<ExtendedFloatingActionButton>(R.id.floatingActionButton)
                        ?.extend()
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        model.allPills.observe(viewLifecycleOwner, {
            pillsAdapter.setPills(it)
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            findNavController()
        ) || super.onOptionsItemSelected(item)
    }
}