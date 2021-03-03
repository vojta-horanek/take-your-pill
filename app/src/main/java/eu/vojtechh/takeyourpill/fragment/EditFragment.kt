package eu.vojtechh.takeyourpill.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.liveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.adapter.ColorAdapter
import eu.vojtechh.takeyourpill.adapter.ReminderAdapter
import eu.vojtechh.takeyourpill.databinding.FragmentEditBinding
import eu.vojtechh.takeyourpill.fragment.dialog.ReminderDialog
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.klass.disableAnimations
import eu.vojtechh.takeyourpill.klass.showError
import eu.vojtechh.takeyourpill.klass.themeColor
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.PillColor
import eu.vojtechh.takeyourpill.model.Reminder
import eu.vojtechh.takeyourpill.reminder.NotificationManager
import eu.vojtechh.takeyourpill.reminder.ReminderManager
import eu.vojtechh.takeyourpill.viewmodel.EditViewModel
import kotlinx.coroutines.Dispatchers
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

@AndroidEntryPoint
class EditFragment : Fragment() {

    private lateinit var binding: FragmentEditBinding
    private val model: EditViewModel by viewModels()
    private val args: EditFragmentArgs by navArgs()

    private val colorAdapter = ColorAdapter()
    private val reminderAdapter = ReminderAdapter()

    private val isCreatingNewPill
        get() = args.pillId == -1L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater, container, false)

        if (isCreatingNewPill) {
            enterTransition = MaterialContainerTransform().apply {
                startView = requireActivity().findViewById(R.id.floatingActionButton)
                endView = binding.layoutEdit
                scrimColor = Color.TRANSPARENT
                containerColor = requireContext().themeColor(R.attr.colorSurface)
                startContainerColor = requireContext().themeColor(R.attr.colorSecondary)
                endContainerColor = requireContext().themeColor(R.attr.colorSurface)
            }

            returnTransition = Slide().addTarget(R.id.layoutEdit)
        } else {
            enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
            returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            onBackPressed()
        }

        if (isCreatingNewPill) {
            binding.textNewPill.text = getString(R.string.new_pill)
            if (!model.isPillInitialized) {
                model.pill = model.getNewEmptyPill()
            }
            binding.pill = model.pill
            model.firstNameEdit = false
            model.firstDescriptionEdit = false
            initViews()
        } else {
            binding.textNewPill.text = getString(R.string.edit_pill)
            postponeEnterTransition()
            if (!model.isPillInitialized) {
                model.getPillById(args.pillId).observe(viewLifecycleOwner, {
                    model.pill = it
                    binding.pill = it
                    initViews()
                    startPostponedEnterTransition()
                })
            } else {
                binding.pill = model.pill
                initViews()
                startPostponedEnterTransition()
            }
        }
    }

    /**
     * Called when imagepicker finishes
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                onNewImagePicked(data?.data!!)
            }
            ImagePicker.RESULT_ERROR -> {
                showSnackbar(ImagePicker.getError(data))
            }
            else -> {
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    private fun initViews() {
        model.initFields()

        model.pillColors.observe(viewLifecycleOwner) {
            colorAdapter.submitList(it)
            setUiColor(model.pill.color)
        }
        model.reminders.observe(viewLifecycleOwner) { reminderAdapter.submitList(it) }

        binding.run {

            refreshImage()
            model.photoBitmap.observe(viewLifecycleOwner) {
                refreshImage()
            }

            recyclerReminders.adapter = reminderAdapter
            reminderAdapter.onReminderClicked { _, reminder -> showReminderDialog(reminder, true) }
            reminderAdapter.onReminderDelete { _, reminder -> model.removeReminder(reminder) }

            recyclerColor.adapter = colorAdapter
            recyclerColor.disableAnimations()

            colorAdapter.onColorClicked { _, pillColor -> model.setActivePillColor(pillColor) }

            pillOptionsView.setOptions(model.pill.options)
            pillOptionsView.onChange {
                model.hasPillBeenEdited = true
            }

            inputName.doOnTextChanged { text, _, _, _ ->
                inputNameLayout.showError(
                    if (model.onNameChanged(text)) getString(R.string.enter_field) else null
                )
            }
            inputDescription.doOnTextChanged { text, _, _, _ -> model.onDescriptionChanged(text) }
            buttonSave.setOnClickListener { onPillSave() }
            buttonAddReminder.setOnClickListener { showReminderDialog() }
            imageChooser.setOnImageClickListener { onPickImage() }
            imageChooser.setOnDeleteClickListener { onImageDelete() }
        }

    }

    private fun refreshImage() {
        binding.imageChooser.setImageDrawable(model.pill.getPhotoDrawable(requireContext()))
        binding.imageChooser.setDeleteVisible(model.pill.isPhotoVisible)
    }

    private fun setUiColor(checkedColor: PillColor) {
        val color = checkedColor.getColor(requireContext())
        val colorStateList = ColorStateList.valueOf(color)
        binding.run {
            buttonSave.backgroundTintList = colorStateList
            listOf(inputName, inputDescription).forEach {
                it.highlightColor = color
            }
            listOf(inputNameLayout, inputDescriptionLayout).forEach {
                it.boxStrokeColor = color
                it.hintTextColor = colorStateList
            }

            buttonAddReminder.strokeColor = colorStateList
            buttonAddReminder.iconTint = colorStateList
            buttonAddReminder.rippleColor = colorStateList
            buttonAddReminder.setTextColor(color)

            progress.setIndicatorColor(color)
            pillOptionsView.setButtonTint(color)
        }
    }

    private fun onBackPressed() {
        if (model.hasPillBeenEdited) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.confirm_exit_edit))
                .setMessage(getString(R.string.confirm_exit_edit_description))
                .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    findNavController().popBackStack()

                    dialog.dismiss()
                }
                .show()

        } else {
            findNavController().popBackStack()
        }
    }

    private fun onImageDelete() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.confirm_delete_photo))
            .setMessage(getString(R.string.confirm_delete_photo_description))
            .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                model.deleteImage()
                dialog.dismiss()
            }
            .show()
    }

    private fun showReminderDialog(
        reminder: Reminder = Reminder.create(pillId = model.pill.id),
        editing: Boolean = false
    ) = ReminderDialog().apply {
        onConfirm { rem, edit -> onNewReminderConfirmed(rem, edit) }
        isEditing = editing
        this.reminder = reminder
        accentColor = model.pill.color
    }.show(childFragmentManager, Constants.TAG_REMINDER_DIALOG)


    private fun onNewReminderConfirmed(reminder: Reminder, editing: Boolean) {
        val reminderDialog =
            childFragmentManager.findFragmentByTag(Constants.TAG_REMINDER_DIALOG) as ReminderDialog

        model.pill.reminders.find { reminder.hasSameTime(it) }?.let {
            // There is a reminder that has the same time
            if ((editing && it != reminder) || !editing) {
                reminderDialog.showError(getString(R.string.two_reminders_same_time))
            }
        } ?: run {
            if (editing) model.editReminder(reminder)
            else model.addReminder(reminder)
            reminderDialog.dismiss()
        }

    }

    @AfterPermissionGranted(Constants.READ_EXTERNAL_STORAGE_PERMISSION_CODE)
    private fun onPickImage() {
        if (EasyPermissions.hasPermissions(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            ImagePicker.with(this)
                .compress(Constants.IMAGE_MAX_SIZE)
                .maxResultSize(Constants.IMAGE_MAX_WIDTH, Constants.IMAGE_MAX_HEIGHT)
                .crop()
                .start()
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.storage_perm_rationale),
                Constants.READ_EXTERNAL_STORAGE_PERMISSION_CODE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun onNewImagePicked(uri: Uri) {
        model.setImage(uri, requireContext()).observe(viewLifecycleOwner) {
            if (!it) {
                showSnackbar(getString(R.string.error_opening_image))
            }
        }
    }

    private fun onPillSave() = binding.run {
        // Does Pill have a name?
        if (model.pill.name.isBlank()) {
            inputNameLayout.error = getString(R.string.enter_field)
            return
        }

        // Does Pill have at least one reminder?
        if (model.pill.reminders.isEmpty()) {
            showSnackbar(getString(R.string.no_reminders_set))
            return
        }

        // TODO Use WorkManager for saving
        layoutLoading.isVisible = true

        model.pill.options = pillOptionsView.getOptions()

        if (isCreatingNewPill) {
            model.addAndGetPill(model.pill).observe(viewLifecycleOwner) {
                setReminding(it) {
                    exitTransition = Slide().addTarget(R.id.layoutEdit)
                    findNavController().popBackStack()
                }
            }
        } else {
            model.updateAndGetPill(model.pill).observe(viewLifecycleOwner) {
                setReminding(it) {
                    returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun setReminding(pill: Pill, action: () -> Unit) {
        Timber.d("lastRemindTime: %s", pill.lastReminderDate)
        liveData(Dispatchers.IO) {
            NotificationManager.createNotificationChannel(
                requireContext(),
                pill.id.toString(),
                pill.name
            )
            ReminderManager.planNextPillReminder(requireContext(), pill)
            emit(true)
        }.observe(viewLifecycleOwner) {
            Timber.d("lastRemindTime: %s", pill.lastReminderDate)
            action()
        }
    }

    private fun showSnackbar(msg: String) =
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
}