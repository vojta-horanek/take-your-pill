package eu.vojtechh.takeyourpill.fragment

import android.Manifest
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import com.kroegerama.imgpicker.BottomSheetImagePicker
import com.kroegerama.imgpicker.ButtonType
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.adapter.ColorAdapter
import eu.vojtechh.takeyourpill.adapter.ReminderAdapter
import eu.vojtechh.takeyourpill.databinding.FragmentEditBinding
import eu.vojtechh.takeyourpill.klass.*
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.PillColor
import eu.vojtechh.takeyourpill.model.Reminder
import eu.vojtechh.takeyourpill.reminder.NotificationManager
import eu.vojtechh.takeyourpill.reminder.ReminderManager
import eu.vojtechh.takeyourpill.reminder.ReminderOptions
import eu.vojtechh.takeyourpill.viewmodel.EditViewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


@AndroidEntryPoint
class EditFragment : Fragment(), ColorAdapter.ColorAdapterListener,
    ReminderAdapter.ReminderAdapterListener, BottomSheetImagePicker.OnImagesSelectedListener,
    BottomSheetFragmentNewReminder.ConfirmListener {

    private lateinit var binding: FragmentEditBinding
    private val model: EditViewModel by viewModels()
    private val args: EditFragmentArgs by navArgs()

    private val colorAdapter = ColorAdapter(this)
    private val reminderAdapter = ReminderAdapter(this)

    private val isPillNew
        get() = args.pillId == -1L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater, container, false)
        if (isPillNew) {
            enterTransition = MaterialContainerTransform().apply {
                startView = requireActivity().findViewById(R.id.floatingActionButton)
                endView = requireActivity().findViewById(R.id.layoutEdit)
                scrimColor = Color.TRANSPARENT
                containerColor = requireContext().themeColor(R.attr.colorSurface)
                startContainerColor = requireContext().themeColor(R.attr.colorSecondary)
                endContainerColor = requireContext().themeColor(R.attr.colorSurface)
            }
            returnTransition = Slide().apply {
                addTarget(R.id.layoutEdit)
            }
        } else {
            enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
            returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isPillNew) {
            binding.textNewPill.text = getString(R.string.new_pill)
            if (!model.isPillInitialized) {
                model.pill = model.getNewEmptyPill()
            }
            binding.pill = model.pill
            initViews()
        } else {
            binding.textNewPill.text = getString(R.string.edit_pill)
            postponeEnterTransition()
            if (!model.isPillInitialized) {
                model.getPillById(args.pillId).observe(viewLifecycleOwner, {
                    model.pill = it
                    binding.pill = model.pill
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

    private fun initViews() {
        model.pillColors.observe(viewLifecycleOwner, {
            colorAdapter.submitList(it)
        })
        model.setActivePillColor(model.pill.color)

        model.setReminders(model.pill.reminders)
        model.reminders.observe(viewLifecycleOwner, {
            reminderAdapter.submitList(it)
        })

        model.photoBitmap.observe(viewLifecycleOwner, {
            binding.imagePillPhoto.setImageDrawable(model.pill.photoDrawable(requireContext()))
            binding.imageDeletePhoto.visibility = model.pill.photoVisibility
        })

        binding.run {
            recyclerColor.adapter = colorAdapter
            recyclerReminders.adapter = reminderAdapter

            setReminderOptionsViews()

            inputName.doOnTextChanged { text, _, _, _ ->
                inputNameLayout.showError(
                    if (text.isNullOrBlank()) getString(R.string.enter_field) else null
                )
                text?.let { model.pill.name = it.trim().toString() }
            }

            buttonSave.setOnClickListener {
                binding.progress.setIndicatorColor(model.pill.color.getColor(requireContext()))
                binding.layoutLoading.visibility = View.VISIBLE
                savePill()
            }
            buttonAddReminder.setOnClickListener { showReminderDialog() }
            imagePillPhoto.setOnClickListener { pickImage() }
            imageDeletePhoto.setOnClickListener { model.deleteImage() }
        }
    }

    override fun onReminderClicked(view: View, reminder: Reminder) {
        showReminderDialog(reminder, editing = true)
    }

    private fun showReminderDialog(
        reminder: Reminder = Reminder.create(pillId = model.pill.id),
        editing: Boolean = false
    ) {
        BottomSheetFragmentNewReminder()
            .setListener(this)
            .setEditing(editing)
            .setReminder(reminder)
            .show(childFragmentManager, "new_reminder")
    }

    override fun onNewReminderClicked(reminder: Reminder, editing: Boolean) {
        val sheet =
            (childFragmentManager.findFragmentByTag("new_reminder") as BottomSheetFragmentNewReminder)
        val potentialMatch =
            model.pill.reminders.find { it.hour == reminder.hour && it.minute == reminder.minute }
        if (editing) {
            if (potentialMatch != null && potentialMatch != reminder) {
                sheet.showError(getString(R.string.two_reminders_same_time))
                return
            }
            model.editReminder(reminder)
        } else {
            if (potentialMatch != null) {
                sheet.showError(getString(R.string.two_reminders_same_time))
                return
            }
            model.addReminder(reminder)

        }
        sheet.dismiss()
    }

    @AfterPermissionGranted(1)
    private fun pickImage() {
        if (EasyPermissions.hasPermissions(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            BottomSheetImagePicker.Builder(getString(R.string.file_provider))
                .cameraButton(ButtonType.Tile)
                .galleryButton(ButtonType.Tile)
                .singleSelectTitle(R.string.select_image)
                .show(childFragmentManager)
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.storage_perm_rationale),
                1,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    override fun onImagesSelected(uris: List<Uri>, tag: String?) {
        uris.forEach { uri ->
            model.setImage(uri, requireContext())
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


    private fun setReminderOptionsViews() {
        binding.run {
            with(model.pill.options) {
                checkDayLimit.isChecked = limitDays != ReminderOptions.NO_DAY_LIMIT
                checkRestoreAfter.isChecked = breakDays != ReminderOptions.NO_BREAK
                checkCycleCount.isChecked = repeatCount == ReminderOptions.REPEAT_FOREVER
            }

            checkDayLimit.setOnCheckedChangeListener { _, b ->
                doOnLimitDayChecked(b)
                scrollToBottom()
            }
            checkRestoreAfter.setOnCheckedChangeListener { _, b ->
                doOnRestoreAfterCheck(b)
                scrollToBottom()
            }
            checkCycleCount.setOnCheckedChangeListener { _, b ->
                doOnCycleCountChecked(b)
                scrollToBottom()
            }

            doOnLimitDayChecked(checkDayLimit.isChecked)
            doOnRestoreAfterCheck(checkRestoreAfter.isChecked)
            doOnCycleCountChecked(checkCycleCount.isChecked)
        }
    }

    private fun scrollToBottom() {
        binding.layoutEdit.post {
            binding.scrollEdit.scrollToBottom()
        }
    }

    private fun doOnLimitDayChecked(checked: Boolean) {
        binding.run {
            groupLimit.setVisible(checked)

            checkRestoreAfter.setVisible(checked)
            if (checked) {
                groupRestore.setVisible(checkRestoreAfter.isChecked)
                checkCycleCount.setVisible(checkRestoreAfter.isChecked)
                groupCycle.setVisible(!checkCycleCount.isChecked)
            } else {
                groupRestore.setVisible(false)
                checkCycleCount.setVisible(false)
                groupCycle.setVisible(false)
            }
        }
    }

    private fun doOnRestoreAfterCheck(checked: Boolean) {
        binding.run {
            groupRestore.setVisible(checked)

            checkCycleCount.setVisible(checked)
            if (checked) {
                groupCycle.setVisible(!checkCycleCount.isChecked)
            } else {
                groupCycle.setVisible(false)
            }
        }
    }

    private fun doOnCycleCountChecked(checked: Boolean) {
        binding.groupCycle.setVisible(!checked)
    }

    private fun savePill() {
        binding.run {
            // Does Pill have name?
            if (model.pill.name.isBlank()) {
                inputNameLayout.error = getString(R.string.enter_field)
                return
            }

            // Does Pill have at leas one reminder?
            if (model.pill.reminders.isEmpty()) {
                Snackbar.make(
                    this.root,
                    getString(R.string.no_reminders_set),
                    Snackbar.LENGTH_SHORT
                ).show()
                return
            }

            val reminderOptions = getReminderOptions()
            model.pill.apply {
                options = reminderOptions
                description = inputDescription.getString()
            }

            if (isPillNew) {
                model.addPillReturn(model.pill).observe(viewLifecycleOwner) {
                    setReminding(it)
                    exitTransition = Slide().apply {
                        addTarget(R.id.layoutEdit)
                    }
                    findNavController().popBackStack()
                }
            } else {
                model.updatePillReturn(model.pill).observe(viewLifecycleOwner) {
                    setReminding(it)
                    returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun setReminding(pill: Pill) {
        NotificationManager.createNotificationChannel(
            requireContext(),
            pill.id.toString(),
            pill.name
        )
        ReminderManager.planNextReminder(requireContext(), pill.reminders)
    }

    private fun getReminderOptions(): ReminderOptions {
        binding.run {
            if (checkDayLimit.isChecked) {
                if (checkRestoreAfter.isChecked) {
                    return if (!checkCycleCount.isChecked) {
                        ReminderOptions.finiteRepeating(
                            inputDayNumber.getNumber(),
                            inputRestore.getNumber(),
                            inputCycleCount.getNumber()
                        )
                    } else {
                        ReminderOptions.infiniteBreak(
                            inputDayNumber.getNumber(),
                            inputRestore.getNumber()
                        )
                    }
                } else {
                    return ReminderOptions.finite(
                        inputDayNumber.getNumber()
                    )
                }
            } else {
                return ReminderOptions.infinite()
            }
        }
    }

    override fun onColorClicked(view: View, color: PillColor) {
        model.setActivePillColor(color)
    }

    override fun onReminderDelete(view: View, reminder: Reminder) {
        model.removerReminder(reminder)
    }

}