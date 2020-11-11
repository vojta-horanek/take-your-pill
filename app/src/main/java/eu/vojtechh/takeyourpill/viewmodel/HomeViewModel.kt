package eu.vojtechh.takeyourpill.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import eu.vojtechh.takeyourpill.repository.PillRepository

class HomeViewModel @ViewModelInject constructor(
    private val pillRepository: PillRepository
) : ViewModel() {
    val allPills = pillRepository.getAllPills()
    var isReturningFromEdit = false
}