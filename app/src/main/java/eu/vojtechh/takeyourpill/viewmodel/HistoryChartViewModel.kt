package eu.vojtechh.takeyourpill.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.BarEntry
import eu.vojtechh.takeyourpill.repository.HistoryRepository

class HistoryChartViewModel @ViewModelInject constructor(
    historyRepository: HistoryRepository
) : ViewModel() {
    val allHistory = historyRepository.getHistory()

    val chartData = mutableListOf<BarEntry>()

}