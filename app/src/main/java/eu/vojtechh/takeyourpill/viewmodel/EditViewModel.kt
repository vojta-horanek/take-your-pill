package eu.vojtechh.takeyourpill.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.PillColor
import eu.vojtechh.takeyourpill.model.Reminder
import eu.vojtechh.takeyourpill.reminder.NotificationManager
import eu.vojtechh.takeyourpill.reminder.ReminderManager
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
        private val pillRepository: PillRepository
) : ViewModel() {

    fun addPill(pill: Pill, applicationContext: Context) = GlobalScope.launch(Dispatchers.IO) {
        val newPill = pillRepository.insertPillReturn(pill)
        setReminding(newPill, applicationContext)
    }

    fun updatePill(pill: Pill, applicationContext: Context) = GlobalScope.launch(Dispatchers.IO) {
        val newPill = pillRepository.updatePillReturn(pill)
        setReminding(newPill, applicationContext)
    }

    private fun setReminding(pill: Pill, applicationContext: Context) {
        NotificationManager.createNotificationChannel(
            applicationContext,
            pill.id.toString(),
            pill.name
        )
        ReminderManager.planNextPillReminder(applicationContext, pill)
    }

    fun getPillById(pillId: Long) = pillRepository.getPillFlow(pillId).asLiveData()

    fun getNewEmptyPill() = Pill.new()

    var hasPillBeenEdited = false
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
            color.isChecked = (color.resource == it.resource)
        }
        colors
    }

    private val _reminders: MutableLiveData<List<Reminder>> by lazy {
        MutableLiveData<List<Reminder>>()
    }

    val reminders = Transformations.map(_reminders) {
        pill.reminders = it.sortedBy { rem -> rem.time.time }.toMutableList()
        pill.reminders
    }

    private val _photoBitmap: MutableLiveData<Bitmap?> by lazy {
        MutableLiveData<Bitmap?>()
    }

    val photoBitmap = Transformations.map(_photoBitmap) {
        pill.photo = it
        it
    }

    fun setActivePillColor(pillColor: PillColor) {
        _activeColor.value = pillColor
        hasPillBeenEdited = true
    }

    fun addReminder(reminder: Reminder) {
        val newList = _reminders.value?.toMutableList()
        newList?.add(reminder)
        hasPillBeenEdited = true
        _reminders.value = newList
    }

    fun removeReminder(reminder: Reminder) {
        val newList = _reminders.value?.toMutableList()
        newList?.remove(reminder)
        hasPillBeenEdited = true
        _reminders.value = newList
    }

    fun editReminder(reminder: Reminder) {
        val newList = _reminders.value?.toMutableList()
        if (reminder.id != 0L) {
            newList?.removeAll { r -> r.id == reminder.id }
        } else {
            newList?.remove(reminder)
        }
        newList?.add(reminder)
        hasPillBeenEdited = true
        _reminders.value = newList
    }

    fun setImage(data: Uri, context: Context) = liveData(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(data)
            _photoBitmap.postValue(BitmapFactory.decodeStream(inputStream))
            hasPillBeenEdited = true
            emit(true)
        } catch (e: FileNotFoundException) {
            emit(false)
        }
    }

    fun deleteImage() {
        hasPillBeenEdited = true
        _photoBitmap.value = null
    }

    fun initFields() {
        _activeColor.value = pill.color
        _reminders.value = pill.reminders
    }

    var firstNameEdit = true
    var firstDescriptionEdit = true

    fun onNameChanged(text: CharSequence?): Boolean {
        text?.let { pill.name = it.trim().toString() }
        if (firstNameEdit) {
            firstNameEdit = false
        } else {
            hasPillBeenEdited = true
        }
        return text.isNullOrBlank()
    }

    fun onDescriptionChanged(text: CharSequence?) {
        text?.let { pill.description = it.trim().toString() }
        if (firstDescriptionEdit) {
            firstDescriptionEdit = false
        } else {
            hasPillBeenEdited = true
        }
    }
}