package eu.vojtechh.takeyourpill.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.PillColor
import eu.vojtechh.takeyourpill.model.Reminder
import eu.vojtechh.takeyourpill.reminder.ReminderOptions
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

    fun getNewEmptyPill() = Pill(
        "",
        "",
        null,
        PillColor.default(),
        ReminderOptions.empty(),
        ReminderOptions.empty()
    )

    lateinit var pill: Pill
    val isPillInitialized
        get() = ::pill.isInitialized

    private val _activeColor: MutableLiveData<PillColor> by lazy {
        MutableLiveData<PillColor>()
    }

    val pillColors = Transformations.map(_activeColor) {
        pill.color = it
        val colors = PillColor.getAllPillColorList()
        for (color in colors) {
            color.checked = (color.resource == it.resource)
        }
        colors
    }

    fun setActivePillColor(pillColor: PillColor) {
        _activeColor.value = pillColor
    }

    fun getActivePillColor() = _activeColor.value!!

    private val _reminders: MutableLiveData<List<Reminder>> by lazy {
        MutableLiveData<List<Reminder>>()
    }

    val reminders = Transformations.map(_reminders) {
        pill.remindConstant.remindTimes = it.toMutableList()
        it
    }

    fun setReminders(reminders: List<Reminder>) {
        _reminders.value = reminders
    }

    fun addReminder(reminder: Reminder) {
        val newList = _reminders.value?.toMutableList()
        newList?.add(reminder)
        _reminders.value = newList
    }

    fun removerReminder(reminder: Reminder) {
        val newList = _reminders.value?.toMutableList()
        newList?.remove(reminder)
        _reminders.value = newList
    }

    fun getReminderTimes(): MutableList<Reminder> {
        return _reminders.value!!.toMutableList()
    }

}