package eu.vojtechh.takeyourpill.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class EditViewModel @ViewModelInject constructor(
    private val pillRepository: PillRepository
) : ViewModel() {
    fun addPill(pill: Pill) = liveData { emit(pillRepository.insertPill(pill)) }
    fun updatePill(pill: Pill) =
        viewModelScope.launch(Dispatchers.IO) { pillRepository.updatePill(pill) }

    fun getPillById(pillId: Long) = pillRepository.getPill(pillId)

    fun getPillBlocking(pillId: Long): Pill? {
        return runBlocking {
            return@runBlocking pillRepository.getPillAsync(pillId)
        }
    }
}