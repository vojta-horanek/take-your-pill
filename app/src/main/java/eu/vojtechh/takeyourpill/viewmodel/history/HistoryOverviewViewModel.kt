package eu.vojtechh.takeyourpill.viewmodel.history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.vojtechh.takeyourpill.model.History
import eu.vojtechh.takeyourpill.model.StatItem
import eu.vojtechh.takeyourpill.repository.HistoryRepository
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class HistoryOverviewViewModel @Inject constructor(
        pillRepository: PillRepository,
        historyRepository: HistoryRepository
) : ViewModel() {
    val allPills = pillRepository.getAllPillsIncludingDeleted()

    val allHistory = historyRepository.getHistoryOrderedById()

    fun getStatsData(history: List<History>, context: Context) =
            liveData(Dispatchers.IO) {
                val statList = mutableListOf<StatItem>()

                val totalReminded = history.size
                val totalConfirmed = history.count { it.hasBeenConfirmed }
                val totalMissed = totalReminded - totalConfirmed

                //statList.add(0, StatItem(totalReminded, totalConfirmed, totalMissed))

                val pillsHistory = history.groupBy { it.pillId }.values
                // Iterate for each pill
                pillsHistory.forEach { pillHistory ->

                    val pillReminded = pillHistory.size
                    val pillConfirmed = pillHistory.count { it.hasBeenConfirmed }
                    val pillMissed = pillReminded - pillConfirmed

                    statList.add(StatItem(pillHistory.first().pillId, pillReminded, pillConfirmed,
                            pillMissed))
                }

                emit(statList)
            }
}