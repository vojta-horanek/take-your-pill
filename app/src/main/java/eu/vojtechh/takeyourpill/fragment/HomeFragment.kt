package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.adapter.AppRecyclerAdapter
import eu.vojtechh.takeyourpill.databinding.FragmentHomeBinding
import eu.vojtechh.takeyourpill.model.BaseModel
import eu.vojtechh.takeyourpill.model.History
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.viewmodel.HomeViewModel

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), AppRecyclerAdapter.ItemListener {

    private val model: HomeViewModel by viewModels()

    private val binding by viewBinding(FragmentHomeBinding::bind)

    private lateinit var appAdapter: AppRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Only postpone when returning from Details
        if (model.isReturningFromPillDetails) {
            exitTransition = MaterialFadeThrough()
            postponeEnterTransition()
            model.isReturningFromPillDetails = false
        }

        appAdapter = AppRecyclerAdapter(
            this, getString(R.string.pills), getString(R.string.try_to_add_a_pill_first),
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_empty_view)
        )

        binding.run {

            floatingActionButton.setOnClickListener {
                openNewPill()
            }

            recyclerHome.adapter = appAdapter

            recyclerHome.setOnScrollChangeListener { _, _, _, _, _ ->
                val offset = recyclerHome.computeVerticalScrollOffset()
                when (floatingActionButton.isExtended) {
                    true -> if (offset > 0) floatingActionButton.shrink()
                    false -> if (offset == 0) floatingActionButton.extend()
                }
            }
        }



        model.allPills.observe(viewLifecycleOwner) { pills ->
            model.addConfirmCards(pills).observe(viewLifecycleOwner) { allPills ->
                appAdapter.submitList(allPills)
                view.doOnPreDraw { startPostponedEnterTransition() }
                model.lastPills = allPills
            }
        }
    }

    private fun openNewPill() {
        exitTransition = null
        reenterTransition = MaterialElevationScale(true)
        model.isReturningFromPillDetails = true
        findNavController().navigate(R.id.edit)
    }

    override fun onItemClicked(view: View, item: BaseModel) {
        if (item is Pill) {
            model.isReturningFromPillDetails = true
            exitTransition = MaterialElevationScale(false)
            reenterTransition = MaterialElevationScale(true)
            val pillDetailTransitionName = getString(R.string.pill_details_transition_name)
            val extras = FragmentNavigatorExtras(view to pillDetailTransitionName)
            val directions = HomeFragmentDirections.actionHomescreenToDetails(item.id)
            findNavController().navigate(directions, extras)
        }
    }

    override fun onPillConfirmClicked(confirmCard: View, history: History) {
        model.confirmPill(requireContext(), history).observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    confirmCard.isVisible = false
                    // Fixes card still visible after next observe
                    model.addConfirmCards(model.lastPills).observe(viewLifecycleOwner) { pills ->
                        appAdapter.submitList(pills)
                    }
                }
                false -> showMessage(getString(R.string.error))
            }
        }

    }

    private fun showMessage(msg: String) {
        Snackbar
            .make(binding.root, msg, Snackbar.LENGTH_SHORT)
            .apply {
                anchorView = requireActivity().findViewById(R.id.bottomNavigation)
            }.show()
    }
}