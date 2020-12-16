package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.adapter.AppRecyclerAdapter
import eu.vojtechh.takeyourpill.databinding.FragmentHomeBinding
import eu.vojtechh.takeyourpill.klass.viewBinding
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.Reminder
import eu.vojtechh.takeyourpill.viewmodel.HomeViewModel

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), AppRecyclerAdapter.PillAdapterListener {

    private val model: HomeViewModel by viewModels()
    private val view by viewBinding(FragmentHomeBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Only postpone when returning from Details
        if (model.isReturningFromPillDetails) {
            exitTransition = MaterialFadeThrough()
            postponeEnterTransition()
            view.doOnPreDraw { startPostponedEnterTransition() }
            model.isReturningFromPillDetails = false
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        val pillsAdapter = AppRecyclerAdapter(this, getString(R.string.pills))

        view.recyclerHome.run {
            adapter = pillsAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        view.floatingActionButton.shrink()
                    } else {
                        view.floatingActionButton.extend()
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
        model.isReturningFromPillDetails = true
        exitTransition = MaterialElevationScale(false)
        reenterTransition = MaterialElevationScale(true)
        val pillDetailTransitionName = getString(R.string.pill_details_transition_name)
        val extras = FragmentNavigatorExtras(view to pillDetailTransitionName)
        val directions = HomeFragmentDirections.actionHomescreenToDetails(pill.id)
        findNavController().navigate(directions, extras)
    }

    // TODO
    override fun onPillConfirmClicked(view: View, reminder: Reminder) {
        Snackbar.make(view, reminder.timeString, Snackbar.LENGTH_SHORT)
            .apply { anchorView = requireActivity().findViewById(R.id.bottomNavigation) }.show()
    }

    // TODO
    override fun onPillNotConfirmClicked(view: View, reminder: Reminder) {
        Snackbar.make(view, "DISMISS" + reminder.timeString, Snackbar.LENGTH_SHORT)
            .apply { anchorView = requireActivity().findViewById(R.id.bottomNavigation) }.show()

    }
}