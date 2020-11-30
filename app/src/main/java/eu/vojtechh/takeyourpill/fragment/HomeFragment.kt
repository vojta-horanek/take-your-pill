package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.adapter.PillAdapter
import eu.vojtechh.takeyourpill.databinding.FragmentHomeBinding
import eu.vojtechh.takeyourpill.klass.viewBinding
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.viewmodel.HomeViewModel

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), PillAdapter.PillAdapterListener {

    private val model: HomeViewModel by viewModels()
    private val view by viewBinding(FragmentHomeBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Only postpone when returning from EditFragment
        if (model.isReturningFromEdit) {
            postponeEnterTransition()
            view.doOnPreDraw { startPostponedEnterTransition() }
            model.isReturningFromEdit = false
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        val pillsAdapter = PillAdapter(this, getString(R.string.pills))

        view.recyclerHome.run {
            adapter = pillsAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        }

        view.floatingActionButton.setOnClickListener {
            exitTransition = MaterialElevationScale(false)
            reenterTransition = MaterialElevationScale(true)
            findNavController().navigate(R.id.edit)
        }

        model.allPills.observe(viewLifecycleOwner, {
            pillsAdapter.submitList(it)
        })
    }

    override fun onPillClicked(view: View, pill: Pill) {
        model.isReturningFromEdit = true
        exitTransition = MaterialElevationScale(false)
        reenterTransition = MaterialElevationScale(true)
        val pillDetailTransitionName = getString(R.string.pill_details_transition_name)
        val extras = FragmentNavigatorExtras(view to pillDetailTransitionName)
        val directions = HomeFragmentDirections.actionHomescreenToDetails(pill.id)
        findNavController().navigate(directions, extras)
    }
}