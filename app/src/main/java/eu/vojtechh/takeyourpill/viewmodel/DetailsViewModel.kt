package eu.vojtechh.takeyourpill.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DetailsViewModel @ViewModelInject constructor(
    private val pillRepository: PillRepository
) : ViewModel() {
    fun getPillById(pillId: Int) = pillRepository.getPill(pillId)
    fun deletePill(pill: Pill) =
        viewModelScope.launch(Dispatchers.IO) { pillRepository.deletePill(pill) }

    fun getPillBlocking(pillId: Int): Pill? {
        return runBlocking {
            return@runBlocking pillRepository.getPillAsync(pillId)
        }
    }
}