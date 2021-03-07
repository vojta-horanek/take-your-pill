package eu.vojtechh.takeyourpill.fragment

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.adapter.ReminderAdapter
import eu.vojtechh.takeyourpill.databinding.FragmentDetailsBinding
import eu.vojtechh.takeyourpill.fragment.dialog.ConfirmationDialog
import eu.vojtechh.takeyourpill.klass.*
import eu.vojtechh.takeyourpill.reminder.NotificationManager
import eu.vojtechh.takeyourpill.viewmodel.DetailsViewModel
import java.util.*

@AndroidEntryPoint
class DetailsFragment : Fragment(),
    ConfirmationDialog.DeleteListener {

    private val model: DetailsViewModel by viewModels()
    private val args: DetailsFragmentArgs by navArgs()
    private lateinit var binding: FragmentDetailsBinding
    private val reminderAdapter = ReminderAdapter(showDelete = false, showRipple = false)

    var launchedFromNotification = false

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

        launchedFromNotification =
            requireArguments().getBoolean(Constants.INTENT_EXTRA_LAUNCHED_FROM_NOTIFICATION, false)

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

            pill?.let { pill ->

                val accent = pill.colorResource(requireContext())
                val accentList = ColorStateList.valueOf(accent)
                listOf(buttonDelete, buttonHistory).forEach {
                    it.setTextColor(accent)
                    it.rippleColor = accentList
                }
                buttonEdit.backgroundTintList = accentList
                buttonTaken.backgroundTintList = accentList

                buttonEdit.setOnClickListener { navigateToEdit() }
                buttonHistory.setOnClickListener { navigateToHistory() }
                buttonDelete.setOnClickListener { navigateToDelete() }

                // If last reminder date is null, then this is the first reminder
                pill.lastReminderDate?.let { lastDate ->
                    // Only add next cycle if this is the first reminder today
                    if (lastDate.DayOfYear != Calendar.getInstance().DayOfYear) {
                        pill.options.nextCycleIteration()
                    }
                }

                with(pill.options) {
                    when {
                        isIndefinite() -> {
                            textIntakeOptions.isVisible = false
                            intakeDaysActive.isVisible = false
                            intakeDaysInactive.isVisible = false
                        }
                        isFinite() -> {
                            intakeDaysInactive.isVisible = false
                            if (isInactive()) {
                                infoDayLimit.text = getString(R.string.inactive)
                            } else {
                                infoDayLimit.text = getString(
                                    R.string.day_limit_format,
                                    todayCycle,
                                    daysActive
                                )
                            }
                        }
                        isCycle() -> {
                            if (isInactive()) {
                                infoDayLimit.text = daysActive.toString()
                                infoResumeAfter.text = getString(
                                    R.string.resume_after_format,
                                    todayCycle - daysActive,
                                    daysInactive
                                )
                            } else {
                                infoDayLimit.text = getString(
                                    R.string.day_limit_format,
                                    todayCycle,
                                    daysActive
                                )
                                infoResumeAfter.text = daysInactive.toString()
                            }
                        }
                    }
                }
            }

            cardPhoto.setOnLongClickListener {
                imageFullscreen.isVisible = true
                true
            }

            imageFullscreen.setOnClickListener {
                imageFullscreen.isVisible = false
            }

            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                if (imageFullscreen.isVisible) {
                    imageFullscreen.isVisible = false
                } else {
                    findNavController().popBackStack()
                }
            }

        }

        model.setReminders(model.pill.reminders)

        model.reminders.observe(viewLifecycleOwner) {
            reminderAdapter.submitList(it) {
                startPostponedEnterTransition()
            }
        }

        model.getLatestHistory(!launchedFromNotification).observe(viewLifecycleOwner) { history ->
            if (history == null) {
                binding.layoutConfirm.isVisible = false
            } else {
                binding.textQuestionTake.text = binding.root.context.getString(
                    R.string.pill_taken_question,
                    history.amount,
                    history.reminded.time.getTimeString(requireContext())
                )
                binding.buttonTaken.setOnClickListener {
                    model.confirmPill(requireContext(), history).observe(viewLifecycleOwner) {
                        when (it) {
                            true -> binding.layoutConfirm.isVisible = false
                            false -> showMessage(getString(R.string.error))
                        }
                    }
                }

            }
        }

    }

    private fun navigateToEdit() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
        findNavController().navigate(
            DetailsFragmentDirections.actionDetailsFragmentToEditFragment(
                model.pill.id
            )
        )
    }

    private fun navigateToHistory() {
        val directions =
            DetailsFragmentDirections.actionDetailsToFragmentHistoryView(model.pill.id)
        findNavController().navigate(directions)
    }


    private fun navigateToDelete() {
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

    private fun showMessage(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
    }
}