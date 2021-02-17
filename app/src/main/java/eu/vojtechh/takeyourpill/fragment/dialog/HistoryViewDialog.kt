package eu.vojtechh.takeyourpill.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shawnlin.numberpicker.NumberPicker
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.adapter.HistoryViewAdapter
import eu.vojtechh.takeyourpill.databinding.DialogHistoryBinding
import eu.vojtechh.takeyourpill.klass.*
import eu.vojtechh.takeyourpill.model.BaseModel
import eu.vojtechh.takeyourpill.model.History
import eu.vojtechh.takeyourpill.viewmodel.history.HistoryItemViewModel
import java.util.*

@AndroidEntryPoint
class HistoryViewDialog :
        RoundedDialogFragment(), HistoryViewAdapter.ItemListener {
    private lateinit var binding: DialogHistoryBinding
    private val args: HistoryViewDialogArgs by navArgs()
    private val model: HistoryItemViewModel by viewModels()

    private var itemJustRemoved = false
    private var itemRemovedPosition = 0

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

        if (args.isOverall) {
            binding.historyViewTitle.text = getString(R.string.stat_overall)
            initViews()
        } else {
            model.getPillById(args.pillId).observe(viewLifecycleOwner, {
                if (it != null) {
                    binding.historyViewTitle.text = it.name
                    initViews()
                }
            })
        }
    }

    private fun initViews() {
        val adapter = HistoryViewAdapter(
                this,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_history)
        )
        binding.recyclerHistoryView.adapter = adapter
        if (args.isOverall) {
            model.getHistory().observe(viewLifecycleOwner) { history ->
                onListObserve(adapter, history, false)
            }
        } else {
            model.getHistoryForPill(args.pillId).observe(viewLifecycleOwner) { history ->
                onListObserve(adapter, history, true)
            }
        }
        binding.buttonDeleteHistory.setOnClickListener {
            showDeleteDialog()
        }
    }

    private fun onListObserve(adapter: HistoryViewAdapter, history: List<History>?,
                              deleteVisibility: Boolean) {
        history?.let {
            binding.buttonDeleteHistory.isVisible = if (deleteVisibility) it.isNotEmpty() else false
            adapter.submitList(it) {
                // Handle item removal correctly (don't remove the date)
                if (itemJustRemoved) {
                    adapter.notifyItemRangeChanged(itemRemovedPosition - 1, 3)
                    itemJustRemoved = false
                }
            }
        }
    }

    private fun showDeleteDialog() {
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.confirm_delete_history))
                .setMessage(getString(R.string.confirm_delete_history_description))
                .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    model.deletePillHistory(args.pillId).observe(viewLifecycleOwner, {
                        dialog.dismiss()
                        this.dismiss()
                    })
                }
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

    override fun onItemOptionsClick(view: View, item: BaseModel, position: Int) {
        if (item is History) {
            val popup = PopupMenu(requireContext(), view)
            popup.inflate(R.menu.item_history_menu)
            popup.forcePopUpMenuToShowIcons()
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
                        itemJustRemoved = true
                        itemRemovedPosition = position
                        true
                    }
                    R.id.historyChangeConfirmTime -> {
                        showChangeConfirmTimeDialog(item)
                        true
                    }
                    R.id.historyChangeAmount -> {
                        showChangeAmountDialog(item)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

    }

    private fun showChangeAmountDialog(item: History) {

        var amount = item.amount

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_change_amount,
                binding.root as ViewGroup, false)

        val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(view)
            setTitle(R.string.change_amount)
            setMessage(getString(R.string.change_amount_format, amount))
            setPositiveButton(R.string.confirm) { dialog, _ ->
                model.setHistoryAmount(item, amount)
                dialog.dismiss()
            }
            setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        }.create()
        val numberPickerAmount = view.findViewById<NumberPicker>(R.id.numberPickerAmount)

        numberPickerAmount.minValue = 1
        numberPickerAmount.maxValue = NumberPickerHelper.getDisplayValues().size
        numberPickerAmount.displayedValues = NumberPickerHelper.getDisplayValues().toTypedArray()
        numberPickerAmount.value = NumberPickerHelper.convertToPosition(amount)
        numberPickerAmount.setOnValueChangedListener { _, _, value ->
            amount = NumberPickerHelper.convertToString(value)
            dialog.setMessage(getString(R.string.change_amount_format, amount))
        }

        dialog.show()
    }

    private fun showChangeConfirmTimeDialog(item: History) {
        item.confirmed?.let {
            val timePicker = Builders.getTimePicker(requireContext(), it.hour, it.minute)
            timePicker.addOnPositiveButtonClickListener {
                onTimePickerConfirmed(timePicker.hour, timePicker.minute, item)
            }
            timePicker.show(childFragmentManager, Constants.TAG_TIME_PICKER_HISTORY_VIEW)
        }
    }

    private fun onTimePickerConfirmed(
            hour: Int,
            minute: Int,
            item: History
    ) {
        val calendar = Calendar.getInstance()
        calendar.hour = hour
        calendar.minute = minute
        model.setHistoryConfirmTime(item, calendar)
    }
}