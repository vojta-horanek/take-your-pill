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

    // Plan all pills when opened, maybe wasteful?
    fun planReminders(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        val pills = pillRepository.getAllPillsSync()
        pills.forEach {
            ReminderManager.planNextPillReminder(context, it)
        }
    }

}