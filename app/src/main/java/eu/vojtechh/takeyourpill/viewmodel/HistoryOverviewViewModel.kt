package eu.vojtechh.takeyourpill.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import eu.vojtechh.takeyourpill.repository.PillRepository

class HistoryOverviewViewModel @ViewModelInject constructor(
    pillRepository: PillRepository
) : ViewModel() {
    val allPills = pillRepository.getAllPillsIncludingDeleted()
}