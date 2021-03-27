package eu.vojtechh.takeyourpill.viewmodel.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.vojtechh.takeyourpill.klass.CallResult
import eu.vojtechh.takeyourpill.model.*
import eu.vojtechh.takeyourpill.repository.HistoryRepository
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HistoryOverviewViewModel @Inject constructor(
    private val pillRepository: PillRepository,
    historyRepository: HistoryRepository
) : ViewModel() {

    val historyStats = historyRepository.getHistoryOrderedByIdFlow().map { history ->
        if (history.isEmpty()) {
            return@map (CallResult.failure<List<BaseModel>>())
        }

        val pills = pillRepository.getAllPillsIncludingDeleted()

        if (pills.isEmpty()) {
            return@map (CallResult.failure<List<BaseModel>>())
        }

        val statList = mutableListOf<StatItem>()

        val totalReminded = history.size
        val totalConfirmed = history.count { it.hasBeenConfirmed }
        val totalMissed = totalReminded - totalConfirmed

        val overallStat = StatItem(null, totalReminded, totalConfirmed, totalMissed)

        val pillsHistory = history.groupBy { it.pillId }.values
        // Iterate over each pill
        pillsHistory.forEach { pillHistory ->

            val pillId = pillHistory.first().pillId
            val pillReminded = pillHistory.size
            val pillConfirmed = pillHistory.count { it.hasBeenConfirmed }
            val pillMissed = pillReminded - pillConfirmed

            statList.add(StatItem(pillId, pillReminded, pillConfirmed, pillMissed))
        }

        val mergedList = sequence {
            yield(HistoryPillItem(HistoryOverallItem(), overallStat))
            pills.forEach { pill ->
                pill.itemType = BaseModel.ItemType.HISTORY
                statList.find { statItem -> statItem.pillId == pill.id }?.let { statItem ->
                    yield(HistoryPillItem(pill, statItem))
                }
            }
        }.toList()

        return@map (CallResult.success(mergedList))
    }.asLiveData()
}