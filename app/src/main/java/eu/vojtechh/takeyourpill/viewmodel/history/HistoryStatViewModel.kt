package eu.vojtechh.takeyourpill.viewmodel.history

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.model.History
import eu.vojtechh.takeyourpill.model.StatItem
import eu.vojtechh.takeyourpill.repository.HistoryRepository
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryStatViewModel @ViewModelInject constructor(
    historyRepository: HistoryRepository,
    private val pillRepository: PillRepository
) : ViewModel() {
    val allHistory = historyRepository.getHistory()
    private suspend fun getPill(pillId: Long) = pillRepository.getPillSync(pillId)

    val stats = MutableLiveData<List<StatItem>>()

    fun computeStatsData(history: List<History>, context: Context) =
        viewModelScope.launch(Dispatchers.IO) {
            val statList = mutableListOf<StatItem>()

            val totalReminded = history.count()
            val totalConfirmed =
                history.filter { history -> history.hasBeenConfirmed }.count()
            val totalMissed = totalReminded - totalConfirmed

            statList.add(
                StatItem(
                    context.getString(R.string.stat_overall),
                    totalReminded,
                    totalConfirmed,
                    totalMissed
                )
            )

            val pillsHistory = history.groupBy { history -> history.pillId }.values

            pillsHistory.forEach { pillHistory ->
                val pill = getPill(pillHistory.first().pillId)

                val pillReminded = pillHistory.count()
                val pillConfirmed =
                    pillHistory.filter { history -> history.hasBeenConfirmed }.count()
                val pillMissed = pillReminded - pillConfirmed

                statList.add(
                    StatItem(
                        pill.name,
                        pillReminded,
                        pillConfirmed,
                        pillMissed,
                        pill.colorResource(context)
                    )
                )

            }

            this@HistoryStatViewModel.stats.postValue(statList)
        }
}
