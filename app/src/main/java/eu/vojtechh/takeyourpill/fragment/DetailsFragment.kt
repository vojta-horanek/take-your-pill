package eu.vojtechh.takeyourpill.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.adapter.ReminderAdapter
import eu.vojtechh.takeyourpill.databinding.FragmentDetailsBinding
import eu.vojtechh.takeyourpill.fragment.dialog.ConfirmationDialog
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.klass.disableAnimations
import eu.vojtechh.takeyourpill.klass.themeColor
import eu.vojtechh.takeyourpill.reminder.NotificationManager
import eu.vojtechh.takeyourpill.viewmodel.DetailsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsFragment : Fragment(),
    ConfirmationDialog.DeleteListener {

    private var fullscreenImageUp: Boolean = false
    private val model: DetailsViewModel by viewModels()
    private val args: DetailsFragmentArgs by navArgs()
    private lateinit var binding: FragmentDetailsBinding
    private val reminderAdapter = ReminderAdapter(showDelete = false, showRipple = false)

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

        var pillId = requireArguments().getLong(Constants.INTENT_EXTRA_PILL_ID, -1L)
        if (pillId == -1L) pillId = args.pillId

        model.getPillById(pillId).observe(viewLifecycleOwner) { pill ->
            pill?.let {
                model.pill = it
                binding.pill = it
                initViews()
            }
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initViews() {

        binding.run {
            cardPhoto.isVisible = model.pill.isPhotoVisible

            recyclerReminders.adapter = reminderAdapter
            recyclerReminders.disableAnimations()

            buttonDelete.setOnClickListener {
                ConfirmationDialog.newInstance(
                    getString(R.string.delete_pill),
                    getString(R.string.delete_only_pil),
                    getString(R.string.delete_pill_and_history),
                    R.drawable.ic_delete,
                    R.drawable.ic_delete_history,
                ).apply {
                    setListener(this@DetailsFragment) // FIXME the listener get deleted when BottomS.. open and theme is changed
                }.show(childFragmentManager, "confirm_delete")
            }

            buttonHistory.setOnClickListener {
                val directions =
                    DetailsFragmentDirections.actionDetailsToFragmentHistoryView(model.pill.id)
                findNavController().navigate(directions)
            }

            buttonEdit.setOnClickListener {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
                findNavController().navigate(
                    DetailsFragmentDirections.actionDetailsFragmentToEditFragment(
                        model.pill.id
                    )
                )
            }

            cardPhoto.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lifecycleScope.launch {
                            fullscreenImageUp = false
                            delay(ViewConfiguration.getLongPressTimeout().toLong())
                            if (!fullscreenImageUp) {
                                imageFullscreen.isVisible = true
                            }
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        fullscreenImageUp = true
                        imageFullscreen.isVisible = false
                    }
                    else -> {
                    }
                }
                true
            }
        }

        model.setReminders(model.pill.reminders)

        model.reminders.observe(viewLifecycleOwner) {
            reminderAdapter.submitList(it) {
                startPostponedEnterTransition()
            }
        }

    }

    override fun onDeletePill(view: View) {
        model.deletePill(model.pill.pillEntity)
        exitOnDelete()
    }

    override fun onDeletePillHistory(view: View) {
        model.deletePillWithHistory(model.pill)
        exitOnDelete()
    }

    private fun exitOnDelete() {
        NotificationManager.removeNotificationChannel(requireContext(), model.pill.id.toString())
        exitTransition = Slide().apply {
            addTarget(R.id.detailsView)
        }
        sharedElementEnterTransition = null
        findNavController().navigateUp()
    }
}