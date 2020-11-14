package eu.vojtechh.takeyourpill.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.PillColor
import eu.vojtechh.takeyourpill.reminder.ReminderOptions
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditViewModel @ViewModelInject constructor(
    private val pillRepository: PillRepository
) : ViewModel() {
    // TODO: Implement the ViewModel
    fun addDummyPill(name: String) = viewModelScope.launch(Dispatchers.IO) {
        pillRepository.insertPill(
            Pill(
                if (name.isBlank()) "DummyPill" else name,
                "This pill has a long description",
                null,
                PillColor(R.drawable.dot_blue),
                ReminderOptions.Infinite(mutableListOf()),
                ReminderOptions.Infinite(mutableListOf())
            )
        )
    }
}