package eu.vojtechh.takeyourpill.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.github.zawadz88.materialpopupmenu.popupMenu
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
    RoundedDialogFragment() {

    private lateinit var binding: DialogHistoryBinding

    private val args: HistoryViewDialogArgs by navArgs()
    private val model: HistoryItemViewModel by viewModels()

    private lateinit var skeleton: Skeleton

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
            model.getPillById(args.pillId).observe(viewLifecycleOwner) {
                if (it != null) {
                    binding.historyViewTitle.text = it.name
                    initViews()
                }
            }
        }
    }

    private fun initViews() {
        val adapter = HistoryViewAdapter(
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_history),
            args.isOverall
        )
        adapter.setItemOptionsClickListener(::onItemOptionsClick)
        binding.recyclerHistoryView.adapter = adapter

        skeleton = binding.recyclerHistoryView.applySkeleton(R.layout.item_history_skeleton)
        skeleton.showShimmer = true
        skeleton.maskCornerRadius = resources.getDimension(R.dimen.standard_corner_radius)
        skeleton.showSkeleton()

        if (args.isOverall) {
            model.namedHistory.observe(viewLifecycleOwner) { history ->
                onListObserve(adapter, history, false)
            }
        } else {
            model.getHistoryForPill(args.pillId).observe(viewLifecycleOwner) { history ->
                onListObserve(adapter, history, true)
            }
        }

        binding.buttonDeleteHistory.onClick {
            showDeleteDialog()
        }
    }

    private fun onListObserve(
        adapter: HistoryViewAdapter, history: List<History>?,
        deleteVisibility: Boolean
    ) {
        history?.let {
            binding.buttonDeleteHistory.isVisible = if (deleteVisibility) it.isNotEmpty() else false
            adapter.submitList(it) {
                skeleton.showOriginal()
                // Handle item removal correctly (don't remove the date)
                if (itemRemovedPosition != -1) {
                    adapter.notifyItemRangeChanged(itemRemovedPosition - 1, 3)
                    itemRemovedPosition = -1
                }
            }
        }
    }

    private fun showDeleteDialog() =
        Builders.getConfirmDialog(
            requireContext(),
            getString(R.string.confirm_delete_history),
            getString(R.string.confirm_delete_history_description),
            { dialog ->
                model.deletePillHistory(args.pillId).observe(viewLifecycleOwner, {
                    dialog.dismiss()
                    this.dismiss()
                })
            })

    private fun onItemOptionsClick(view: View, historyItem: BaseModel, position: Int) {
        if (historyItem is History) {
            popupMenu {
                if (requireContext().isDarkThemeOn()) {
                    style = R.style.Widget_MPM_Menu_Dark
                }
                section {
                    title = getString(R.string.edit)
                    if (historyItem.hasBeenConfirmed) {
                        item {
                            labelRes = R.string.mark_as_skipped
                            icon = R.drawable.ic_cancel
                            callback = { model.markHistoryNotConfirmed(historyItem) }
                        }
                        item {
                            labelRes = R.string.change_confirm_time
                            icon = R.drawable.ic_time
                            callback = { showChangeConfirmTimeDialog(historyItem) }
                        }
                    } else {
                        item {
                            labelRes = R.string.mark_as_confirmed
                            icon = R.drawable.ic_check
                            callback = { model.confirmHistory(historyItem) }
                        }
                    }

                    item {
                        labelRes = R.string.change_amount
                        icon = R.drawable.ic_pill
                        callback = { showChangeAmountDialog(historyItem) }
                    }
                }
                section {
                    title = getString(R.string.other)
                    item {
                        icon = R.drawable.ic_delete
                        labelRes = R.string.delete
                        callback = {
                            model.deleteHistory(historyItem)
                            itemRemovedPosition = position
                        }
                    }
                }
            }.show(requireContext(), view)
        }
    }

    private fun showChangeAmountDialog(item: History) =
        Builders.getAmountPickerDialog(
            requireContext(),
            binding.root as ViewGroup,
            item.amount
        ) {
            model.setHistoryAmount(item, it)
        }.show()

    private fun showChangeConfirmTimeDialog(item: History) =
        item.confirmed?.let {
            val timePicker = Builders.getTimePicker(requireContext(), it.hour, it.minute)
            timePicker.addOnPositiveButtonClickListener {
                onTimePickerConfirmed(timePicker.hour, timePicker.minute, item)
            }
            timePicker.show(childFragmentManager, Constants.TAG_TIME_PICKER_HISTORY_VIEW)
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