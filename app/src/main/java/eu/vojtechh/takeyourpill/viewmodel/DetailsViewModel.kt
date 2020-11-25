package eu.vojtechh.takeyourpill.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.runBlocking

class DetailsViewModel @ViewModelInject constructor(
    private val pillRepository: PillRepository
) : ViewModel() {
    fun getPillById(pillId: Int) = pillRepository.getPill(pillId)

    fun getPillBlocking(pillId: Int): Pill? {
        return runBlocking {
            return@runBlocking pillRepository.getPillAsync(pillId)
        }
    }
}