package eu.vojtechh.takeyourpill.viewmodel.history

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.vojtechh.takeyourpill.repository.PillRepository
import javax.inject.Inject

@HiltViewModel
class HistoryOverviewViewModel @Inject constructor(
        pillRepository: PillRepository
) : ViewModel() {
    val allPills = pillRepository.getAllPillsIncludingDeleted()
}