package eu.vojtechh.takeyourpill.fragment.dialog

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.adapter.HistoryViewAdapter
import eu.vojtechh.takeyourpill.databinding.DialogHistoryBinding
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.klass.hour
import eu.vojtechh.takeyourpill.klass.minute
import eu.vojtechh.takeyourpill.model.GeneralRecyclerItem
import eu.vojtechh.takeyourpill.model.History
import eu.vojtechh.takeyourpill.viewmodel.HistoryItemViewModel
import java.util.*

@AndroidEntryPoint
class HistoryViewDialog :
    RoundedDialogFragment(), HistoryViewAdapter.ItemListener {
    private lateinit var binding: DialogHistoryBinding
    private val args: HistoryViewDialogArgs by navArgs()
    private val model: HistoryItemViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.getPillById(args.pillId).observe(viewLifecycleOwner, {
            if (it != null) {
                binding.pill = it
                initViews()
            }
        })
    }

    private fun initViews() {
        val adapter = HistoryViewAdapter(
            this,
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_history)
        )
        binding.recyclerHistoryView.adapter = adapter
        model.getHistoryForPill(args.pillId).observe(viewLifecycleOwner, {
            if (it != null) {
                adapter.submitList(it)
                binding.layoutLoading.isVisible = false
            }
        })
    }

    override fun onItemOptionsClick(view: View, item: GeneralRecyclerItem) {
        if (item is History) {
            val popup = PopupMenu(requireContext(), view)
            popup.inflate(R.menu.item_history_menu)
            if (item.hasBeenConfirmed) {
                popup.menu.findItem(R.id.historyConfirm).isVisible = false
            } else {
                popup.menu.findItem(R.id.historyUnConfirm).isVisible = false
                popup.menu.findItem(R.id.historyChangeConfirmTime).isVisible = false
            }
            popup.setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.historyConfirm -> {
                        model.confirmHistory(item)
                        true
                    }
                    R.id.historyUnConfirm -> {
                        model.markHistoryNotConfirmed(item)
                        true
                    }
                    R.id.historyDelete -> {
                        model.deleteHistory(item)
                        true
                    }
                    R.id.historyChangeConfirmTime -> {
                        showChangeConfirmTimeDialog(item)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

    }

    private fun showChangeConfirmTimeDialog(item: History) {
        item.historyEntity.confirmed?.let {
            val format =
                if (DateFormat.is24HourFormat(requireContext()))
                    TimeFormat.CLOCK_24H
                else
                    TimeFormat.CLOCK_12H

            val materialTimePicker = MaterialTimePicker.Builder()
                .setTimeFormat(format)
                .setHour(it.hour)
                .setMinute(it.minute)
                .build()

            materialTimePicker.addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance()
                calendar.hour = materialTimePicker.hour
                calendar.minute = materialTimePicker.minute
                model.setHistoryConfirmTime(item, calendar)
            }

            materialTimePicker.show(childFragmentManager, Constants.TAG_TIME_PICKER_HISTORY_VIEW)
        }
    }
}