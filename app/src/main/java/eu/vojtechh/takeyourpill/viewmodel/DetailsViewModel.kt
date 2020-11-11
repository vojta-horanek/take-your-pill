package eu.vojtechh.takeyourpill.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import eu.vojtechh.takeyourpill.repository.PillRepository

class DetailsViewModel @ViewModelInject constructor(
    private val pillRepository: PillRepository
) : ViewModel() {
    fun getPillById(pillId: Int) = pillRepository.getPill(pillId)
}