package eu.vojtechh.takeyourpill.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.vojtechh.takeyourpill.reminder.ReminderManager
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val pillRepository: PillRepository
) : ViewModel() {

    var confirmedPillId = -1L // Not the most elegant way but hmm :D

    // Plan all pills when opened, maybe wasteful?
    fun planReminders(applicationContext: Context) = viewModelScope.launch(Dispatchers.Main) {
        pillRepository.getAllPills().forEach {
            ReminderManager.planNextPillReminder(applicationContext, it)
        }
    }

}