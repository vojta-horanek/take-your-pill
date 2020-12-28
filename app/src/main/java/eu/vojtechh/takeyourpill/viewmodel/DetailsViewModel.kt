package eu.vojtechh.takeyourpill.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.PillEntity
import eu.vojtechh.takeyourpill.model.Reminder
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsViewModel @ViewModelInject constructor(
    private val pillRepository: PillRepository
) : ViewModel() {
    fun getPillById(pillId: Long) = pillRepository.getPill(pillId)
    fun deletePillWithHistory(pill: Pill) =
        viewModelScope.launch(Dispatchers.IO) { pillRepository.deletePillAndReminder(pill) }

    fun deletePill(pillEntity: PillEntity) =
        viewModelScope.launch(Dispatchers.IO) { pillRepository.markPillDeleted(pillEntity) }

    lateinit var pill: Pill

    private val _reminders: MutableLiveData<List<Reminder>> by lazy {
        MutableLiveData<List<Reminder>>()
    }

    val reminders = Transformations.map(_reminders) {
        pill.reminders = it.sortedBy { rem -> rem.calendar.time }.toMutableList()
        pill.reminders
    }

    fun setReminders(reminders: List<Reminder>) {
        _reminders.value = reminders
    }
}