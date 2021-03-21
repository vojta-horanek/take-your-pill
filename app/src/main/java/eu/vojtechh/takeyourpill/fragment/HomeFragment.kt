package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import eu.vojtechh.takeyourpill.klass.applicationContext
import eu.vojtechh.takeyourpill.klass.onClick
import eu.vojtechh.takeyourpill.model.BaseModel
import eu.vojtechh.takeyourpill.model.History
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.viewmodel.HomeViewModel
import eu.vojtechh.takeyourpill.viewmodel.MainViewModel

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val model: HomeViewModel by viewModels()
    private val mainModel: MainViewModel by activityViewModels()


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
            view.doOnPreDraw { startPostponedEnterTransition() }
            model.isReturningFromPillDetails = false
        }

        appAdapter = AppRecyclerAdapter(
            getString(R.string.pills), getString(R.string.try_to_add_a_pill_first),
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_empty_view)
        )

        appAdapter.setOnItemClickListener(::onPillClicked)
        appAdapter.setOnPillConfirmClickListener(::onConfirmClicked)

        binding.run {

            floatingActionButton.onClick { openNewPill() }

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
            appAdapter.submitList(pills)
        }

        if (mainModel.confirmedPillId != -1L) {
            model.refreshPills(mainModel.confirmedPillId)
            mainModel.confirmedPillId = -1L
        }
    }

    private fun openNewPill() {
        exitTransition = MaterialElevationScale(false)
        reenterTransition = MaterialElevationScale(true)
        findNavController().navigate(R.id.edit)
    }

    private fun onPillClicked(view: View, item: BaseModel) {
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

    private fun onConfirmClicked(history: History) {
        model.confirmPill(applicationContext, history).observe(viewLifecycleOwner) {
            when (it) {
                true -> model.refreshPills()
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