package eu.vojtechh.takeyourpill.viewmodel.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.vojtechh.takeyourpill.klass.CallResult
import eu.vojtechh.takeyourpill.model.*
import eu.vojtechh.takeyourpill.repository.HistoryRepository
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class HistoryOverviewViewModel @Inject constructor(
    private val pillRepository: PillRepository,
    historyRepository: HistoryRepository
) : ViewModel() {

    val history = historyRepository.getHistoryOrderedById()

    fun getStatsData(_history: List<History>) = liveData(Dispatchers.IO) {

        if (_history.isEmpty()) {
            emit(CallResult.failure<List<BaseModel>>())
            return@liveData
        }

        val pills = pillRepository.getAllPillsIncludingDeletedSync()

        if (pills.isEmpty()) {
            emit(CallResult.failure<List<BaseModel>>())
            return@liveData
        }

        val statList = mutableListOf<StatItem>()

        val totalReminded = _history.size
        val totalConfirmed = _history.count { it.hasBeenConfirmed }
        val totalMissed = totalReminded - totalConfirmed

        val overallStat = StatItem(null, totalReminded, totalConfirmed, totalMissed)

        val pillsHistory = _history.groupBy { it.pillId }.values
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
                pill.itemType = BaseModel.ItemTypes.HISTORY
                statList.find { statItem -> statItem.pillId == pill.id }?.let { statItem ->
                    yield(HistoryPillItem(pill, statItem))
                }
            }
        }.toList()

        emit(CallResult.success(mergedList))
    }
}