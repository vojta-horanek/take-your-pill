package eu.vojtechh.takeyourpill.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.postDelayed
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.adapter.ColorAdapter
import eu.vojtechh.takeyourpill.adapter.ReminderAdapter
import eu.vojtechh.takeyourpill.databinding.FragmentEditBinding
import eu.vojtechh.takeyourpill.klass.*
import eu.vojtechh.takeyourpill.model.PillColor
import eu.vojtechh.takeyourpill.model.Reminder
import eu.vojtechh.takeyourpill.reminder.ReminderOptions
import eu.vojtechh.takeyourpill.viewmodel.EditViewModel
import java.util.*

@AndroidEntryPoint
class EditFragment : Fragment(), ColorAdapter.ColorAdapterListener,
    ReminderAdapter.ReminderAdapterListener {

    private val model: EditViewModel by viewModels()
    private val args: EditFragmentArgs by navArgs()
    private val colorAdapter = ColorAdapter(this)
    private val reminderAdapter = ReminderAdapter(this)

    private lateinit var binding: FragmentEditBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater, container, false)
        if (args.pillId == -1L) {
            enterTransition = MaterialContainerTransform().apply {
                startView = requireActivity().findViewById(R.id.floatingActionButton)
                endView = requireActivity().findViewById(R.id.editView)
                scrimColor = Color.TRANSPARENT
                containerColor = requireContext().themeColor(R.attr.colorSurface)
                startContainerColor = requireContext().themeColor(R.attr.colorSecondary)
                endContainerColor = requireContext().themeColor(R.attr.colorSurface)
            }
            returnTransition = Slide().apply {
                addTarget(R.id.editView)
            }
        } else {
            enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
            returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Opened with existing pill
        if (args.pillId != -1L) {
            binding.textNewPill.text = getString(R.string.edit_pill)
            postponeEnterTransition()
            if (model.pill == null) {
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
        } // Opened with new pill
        else {
            binding.textNewPill.text = getString(R.string.new_pill)
            if (model.pill == null) {
                model.pill = model.getNewEmptyPill()
            }
            binding.pill = model.pill
            initViews()
        }
    }

    private fun initViews() {
        model.pillColors.observe(viewLifecycleOwner, {
            colorAdapter.submitList(it)
        })
        model.setActivePillColor(model.pill!!.color)

        model.setReminders(model.pill!!.remindConstant.remindTimes)
        model.reminders.observe(viewLifecycleOwner, {
            reminderAdapter.submitList(it)
        })

        binding.run {
            recyclerColor.adapter = colorAdapter
            recyclerViewReminderTimes.adapter = reminderAdapter

            setReminderViews()

            inputName.doOnTextChanged { text, _, _, _ ->
                inputNameLayout.showError(
                    if (text.isNullOrBlank()) getString(R.string.enter_field) else null
                )
                text?.let { model.pill!!.name = it.trim().toString() }
            }

            buttonSave.setOnClickListener { savePill() }
            buttonAddReminder.setOnClickListener { showTimeDialog() }
        }
    }

    private fun showTimeDialog() {
        val format =
            if (android.text.format.DateFormat.is24HourFormat(requireContext())) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val materialTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(format)
            .build()
        materialTimePicker.addOnPositiveButtonClickListener {
            addRemindTime(materialTimePicker.hour, materialTimePicker.minute)
        }
        materialTimePicker.show(childFragmentManager, "time_picker")
    }

    private fun addRemindTime(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val reminder = Reminder(calendar, 1)
        model.addReminder(reminder)
    }

    private fun setReminderViews() {
        binding.run {
            with(model.pill!!.remindConstant) {
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
        binding.editView.postDelayed(300) {
            binding.editView.scrollToBottom()
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
            if (model.pill!!.name.isBlank()) {
                inputNameLayout.error = getString(R.string.enter_field)
                return
            }
            returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)

            val reminderOptions = getReminderOptions()
            model.pill!!.apply {
                remindConstant = reminderOptions
                description = inputDescription.getString()
            }

            if (args.pillId == -1L) {
                model.addPill(model.pill!!).observe(viewLifecycleOwner) {
                    findNavController().popBackStack()
                    val directions = HomeFragmentDirections.actionHomescreenToDetails(it, true)
                    findNavController().navigate(directions)
                }
            } else {
                model.updatePill(model.pill!!)
                findNavController().popBackStack()
            }
        }
    }

    private fun getReminderOptions(): ReminderOptions {
        binding.run {
            if (checkDayLimit.isChecked) {
                if (checkRestoreAfter.isChecked) {
                    return if (!checkCycleCount.isChecked) {
                        ReminderOptions.finiteRepeating(
                            model.getReminderTimes(),
                            inputDayNumber.getNumber(),
                            inputRestore.getNumber(),
                            inputCycleCount.getNumber()
                        )
                    } else {
                        ReminderOptions.infiniteBreak(
                            model.getReminderTimes(),
                            inputDayNumber.getNumber(),
                            inputRestore.getNumber()
                        )
                    }
                } else {
                    return ReminderOptions.finite(
                        model.getReminderTimes(),
                        inputDayNumber.getNumber()
                    )
                }
            } else {
                return ReminderOptions.infinite(model.getReminderTimes())
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