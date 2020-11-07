package eu.vojtechh.takeyourpill.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.PillColor
import eu.vojtechh.takeyourpill.reminder.ReminderOptions
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    private val pillRepository: PillRepository
) : ViewModel() {
    val allPills = pillRepository.getAllPills()

    fun addDummyPill() = viewModelScope.launch(Dispatchers.IO) {
        pillRepository.insertPill(
            Pill(
                "DummyPill",
                "This pill has a long description",
                null,
                PillColor.RED,
                ReminderOptions.Infinite(mutableListOf()),
                ReminderOptions.Infinite(mutableListOf())
            )
        )
    }
}