package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import eu.vojtechh.takeyourpill.model.GeneralRecyclerItem
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.Reminder
import eu.vojtechh.takeyourpill.viewmodel.HomeViewModel

@AndroidEntryPoint
class HomeFragment : Fragment(), AppRecyclerAdapter.ItemListener {

    private val model: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

        val appAdapter = AppRecyclerAdapter(
            this, getString(R.string.pills), getString(R.string.try_to_add_a_pill_first),
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_empty_view)
        )

        binding.recyclerHome.run {
            adapter = appAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        binding.floatingActionButton.shrink()
                    } else {
                        binding.floatingActionButton.extend()
                    }
                    super.onScrolled(recyclerView, dx, dy)
                }
            })
        }

        binding.floatingActionButton.setOnClickListener {
            openNewPill()
        }

        model.allPills.observe(viewLifecycleOwner, {
            appAdapter.submitList(it)
        })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun openNewPill() {
        exitTransition = MaterialElevationScale(false)
        reenterTransition = MaterialElevationScale(true)
        model.isReturningFromPillDetails = true
        findNavController().navigate(R.id.edit)
    }

    override fun onItemClicked(view: View, item: GeneralRecyclerItem) {
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

    override fun onPillConfirmClicked(view: View, reminder: Reminder) {
        model.confirmPill(reminder)
        Snackbar.make(view, getString(R.string.confirmed), Snackbar.LENGTH_SHORT)
            .apply { anchorView = requireActivity().findViewById(R.id.bottomNavigation) }.show()
    }

    override fun onPillNotConfirmClicked(view: View, reminder: Reminder) {
        Snackbar.make(view, "DISMISS" + reminder.timeString, Snackbar.LENGTH_SHORT)
            .apply { anchorView = requireActivity().findViewById(R.id.bottomNavigation) }.show()

    }
}