package eu.vojtechh.takeyourpill.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import eu.vojtechh.takeyourpill.repository.HistoryRepository
import eu.vojtechh.takeyourpill.repository.PillRepository

class HistoryViewViewModel @ViewModelInject constructor(
    private val pillRepository: PillRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {
    fun getPillById(pillId: Long) = pillRepository.getPill(pillId)
    fun getHistoryForPill(pillId: Long) = historyRepository.getHistoryForPill(pillId)
}