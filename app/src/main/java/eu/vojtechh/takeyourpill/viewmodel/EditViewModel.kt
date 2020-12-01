package eu.vojtechh.takeyourpill.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.PillColor
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
        ReminderOptions.Empty(),
        ReminderOptions.Empty()
    )


    private val _activeColor: MutableLiveData<PillColor> by lazy {
        MutableLiveData<PillColor>(PillColor.default())
    }

    val pillColors: LiveData<List<PillColor>> = Transformations.map(_activeColor) {
        val colors = PillColor.getAllPillColorList()
        for (color in colors) {
            color.checked = (color.resource == it.resource)
        }
        colors
    }

    fun setActivePillColor(pillColor: PillColor) {
        _activeColor.value = pillColor
        pill?.color = pillColor
    }

    fun getActivePillColor() = _activeColor.value!!

    var pill: Pill? = null
}