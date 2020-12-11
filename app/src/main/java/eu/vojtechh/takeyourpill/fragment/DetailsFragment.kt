package eu.vojtechh.takeyourpill.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.adapter.ReminderAdapter
import eu.vojtechh.takeyourpill.databinding.FragmentDetailsBinding
import eu.vojtechh.takeyourpill.klass.themeColor
import eu.vojtechh.takeyourpill.viewmodel.DetailsViewModel

@AndroidEntryPoint
class DetailsFragment : Fragment(),
    BottomSheetFragmentConfirmation.ConfirmListener, ReminderAdapter.ReminderAdapterListener {

    private val model: DetailsViewModel by viewModels()
    private val args: DetailsFragmentArgs by navArgs()
    private lateinit var binding: FragmentDetailsBinding
    private val reminderAdapter = ReminderAdapter(this, showDelete = false, showRipple = false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.navHostFragment
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }

        binding = FragmentDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()

        model.getPillById(args.pillId).observe(viewLifecycleOwner, {
            if (it != null) {
                model.pill = it
                binding.pill = model.pill
                initViews()
            }
        })

    }

    private fun initViews() {

        binding.cardPhoto.visibility = model.pill.photoVisibility
        binding.recyclerReminders.adapter = reminderAdapter

        binding.buttonDelete.setOnClickListener {
            BottomSheetFragmentConfirmation.newInstance(
                getString(R.string.delete_pill),
                getString(R.string.delete_only_pil),
                getString(R.string.delete_pill_and_history),
                R.drawable.ic_delete,
                R.drawable.ic_delete_history,
            ).apply {
                setListener(this@DetailsFragment) // FIXME the listener get deleted when BottomS.. open and theme is changed
            }.show(childFragmentManager, "confirm_delete")
        }

        binding.buttonEdit.setOnClickListener {
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
            val directions =
                DetailsFragmentDirections.actionDetailsFragmentToEditFragment(model.pill.id)
            findNavController().navigate(directions)
        }

        model.setReminders(model.pill.reminders)
        model.reminders.observe(viewLifecycleOwner, {
            reminderAdapter.submitList(it)
            (binding.root.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }
        })

    }

    override fun onConfirmClicked(view: View) {
        model.deletePill(model.pill.pill)
        exitOnDelete()
    }

    override fun onCancelClicked(view: View) {
        model.deletePillWithHistory(model.pill)
        exitOnDelete()
    }

    private fun exitOnDelete() {
        exitTransition = Slide().apply {
            addTarget(R.id.detailsView)
        }
        sharedElementEnterTransition = null
        findNavController().navigateUp()
    }
}