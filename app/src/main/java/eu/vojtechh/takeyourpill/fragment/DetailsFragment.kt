package eu.vojtechh.takeyourpill.fragment

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.adapter.ReminderAdapter
import eu.vojtechh.takeyourpill.databinding.FragmentDetailsBinding
import eu.vojtechh.takeyourpill.fragment.dialog.DeleteDialog
import eu.vojtechh.takeyourpill.klass.*
import eu.vojtechh.takeyourpill.reminder.NotificationManager
import eu.vojtechh.takeyourpill.viewmodel.DetailsViewModel
import eu.vojtechh.takeyourpill.viewmodel.MainViewModel
import java.util.*

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val model: DetailsViewModel by viewModels()
    private val mainModel: MainViewModel by activityViewModels()

    private val args: DetailsFragmentArgs by navArgs()
    private val binding by viewBinding(FragmentDetailsBinding::bind)
    private val reminderAdapter = ReminderAdapter(showDelete = false, showRipple = false)

    var launchedFromNotification = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.navHostFragment
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }

        postponeEnterTransition()

        launchedFromNotification =
            requireArguments().getBoolean(Constants.INTENT_EXTRA_LAUNCHED_FROM_NOTIFICATION, false)

        var pillId = requireArguments().getLong(Constants.INTENT_EXTRA_PILL_ID, -1L)
        if (pillId == -1L) pillId = args.pillId

        model.getPillById(pillId).observe(viewLifecycleOwner) { pill ->
            model.pill = pill
            initViews()
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initViews() {

        binding.run {

            textPillName.text = model.pill.name
            viewPillColor.setBackgroundColorShaped(model.pill.colorResource(requireContext()))

            textPillDescription.apply {
                text = model.pill.description
                isVisible = model.pill.isDescriptionVisible
            }

            val photoDrawable = model.pill.getPhotoDrawable(requireContext())
            imagePillPhoto.setImageDrawable(photoDrawable)
            imageViewFullScreen.setImageDrawable(photoDrawable)

            cardPhoto.isVisible = model.pill.isPhotoVisible

            recyclerReminders.adapter = reminderAdapter
            recyclerReminders.disableAnimations()

            val accent = model.pill.colorResource(requireContext())
            val accentList = ColorStateList.valueOf(accent)
            listOf(buttonDelete, buttonHistory).forEach {
                it.setTextColor(accent)
                it.rippleColor = accentList
            }
            buttonEdit.backgroundTintList = accentList
            buttonTaken.backgroundTintList = accentList

            buttonEdit.onClick { navigateToEdit() }
            buttonHistory.onClick { navigateToHistory() }
            buttonDelete.onClick { navigateToDelete() }

            // If last reminder date is null, then this is the first reminder
            model.pill.lastReminderDate?.let { lastDate ->
                // Only add next cycle if this is the first reminder today
                if (lastDate.DayOfYear != Calendar.getInstance().DayOfYear) {
                    model.pill.options.nextCycleIteration()
                }
            }

            with(model.pill.options) {
                when {
                    isIndefinite() -> {
                    }
                    isFinite() -> {
                        intakeDaysActive.isVisible = true
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
                        intakeDaysActive.isVisible = true
                        intakeDaysInactive.isVisible = true
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

            model.getLastReminded(model.pill.id).observe(viewLifecycleOwner) { history ->
                history?.let {
                    intakeLastReminded.isVisible = true
                    infoLastReminded.text = history.reminded.time.getDateTimeString()
                } ?: run {
                    textIntakeOptions.isVisible = intakeDaysActive.isVisible
                }
            }

            cardPhoto.setOnLongClickListener {
                imageFullscreen.isVisible = true
                true
            }

            imageFullscreen.onClick {
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

        model.getLatestHistory(!launchedFromNotification)
            .observe(viewLifecycleOwner) { history ->
                if (history != null) {
                    binding.layoutConfirm.isVisible = true
                    binding.textQuestionTake.text = binding.root.context.getString(
                        R.string.pill_taken_question,
                        history.amount,
                        history.reminded.time.getTimeString(requireContext())
                    )
                    binding.buttonTaken.onClick {
                        model.confirmPill(requireContext(), history)
                            .observe(viewLifecycleOwner) {
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
            DetailsFragmentDirections.actionDetailsFragmentToEditFragment(model.pill.id)
        )
    }

    private fun navigateToHistory() =
        findNavController().navigate(
            DetailsFragmentDirections.actionDetailsToFragmentHistoryView(model.pill.id)
        )


    private fun navigateToDelete() {
        DeleteDialog().apply {
            setUserListener { what ->
                when (what) {
                    true -> model.deletePillWithHistory(model.pill)
                    false -> model.deletePill(model.pill.pillEntity)
                }
                exitOnDelete()
            }
        }.show(childFragmentManager, "confirm_delete")
    }


    private fun exitOnDelete() {
        NotificationManager.removeNotificationChannel(
            requireContext(),
            model.pill.id.toString()
        )

        model.pill.reminders.forEach {
            NotificationManager.cancelNotification(requireContext(), it.id)
        }

        exitTransition = Slide().apply {
            addTarget(R.id.detailsView)
        }
        sharedElementEnterTransition = null
        findNavController().navigateUp()
    }

    private fun showMessage(msg: String) =
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
}