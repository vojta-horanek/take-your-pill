package eu.vojtechh.takeyourpill.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.PillColor
import eu.vojtechh.takeyourpill.model.Reminder
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.Dispatchers
import java.io.FileNotFoundException
import java.io.InputStream

class EditViewModel @ViewModelInject constructor(
    private val pillRepository: PillRepository
) : ViewModel() {
    fun addPill(pill: Pill) = liveData { emit(pillRepository.insertPill(pill)) }

    fun addAndGetPill(pill: Pill) =
        liveData(Dispatchers.IO) { emitSource(pillRepository.insertPillReturn(pill)) }

    fun updatePill(pill: Pill) = liveData { emit(pillRepository.updatePill(pill)) }

    fun updateAndGetPill(pill: Pill) =
        liveData(Dispatchers.IO) { emitSource(pillRepository.updatePillReturn(pill)) }

    fun getPillById(pillId: Long) = pillRepository.getPill(pillId)

    fun getNewEmptyPill() = Pill.getEmpty()

    lateinit var pill: Pill
    val isPillInitialized
        get() = ::pill.isInitialized

    private val _activeColor = MutableLiveData(PillColor.default())

    val pillColors = Transformations.map(_activeColor) {
        pill.color = it
        val colors = PillColor.getAllPillColorList()
        for (color in colors) {
            color.isChecked = (color.resource == it.resource)
        }
        colors
    }

    fun setActivePillColor(pillColor: PillColor) {
        _activeColor.value = pillColor
    }

    private val _reminders = MutableLiveData(listOf<Reminder>())

    val reminders = Transformations.map(_reminders) {
        pill.reminders = it.sortedBy { rem -> rem.time.time }.toMutableList()
        pill.reminders
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

    fun editReminder(reminder: Reminder) {
        val newList = _reminders.value?.toMutableList()
        if (reminder.id != 0L) {
            newList?.removeAll { r -> r.id == reminder.id }
        } else {
            newList?.remove(reminder)
        }
        newList?.add(reminder)
        _reminders.value = newList
    }

    fun getReminderTimes(): MutableList<Reminder> {
        return _reminders.value!!.toMutableList()
    }

    private val _photoBitmap: MutableLiveData<Bitmap?> by lazy {
        MutableLiveData<Bitmap?>()
    }

    val photoBitmap = Transformations.map(_photoBitmap) {
        pill.photo = it
        it
    }

    fun setImage(data: Uri, context: Context) = liveData(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(data)
            _photoBitmap.postValue(BitmapFactory.decodeStream(inputStream))
            emit(true)
        } catch (e: FileNotFoundException) {
            emit(false)
        }
    }

    fun deleteImage() {
        _photoBitmap.value = null
    }

    fun initFields() {
        setActivePillColor(pill.color)
        setReminders(pill.reminders)
    }

    fun onNameChanged(text: CharSequence?): Boolean {
        text?.let { pill.name = it.trim().toString() }
        return text.isNullOrBlank()
    }

    fun onDescriptionChanged(text: CharSequence?) =
        text?.let { pill.description = it.trim().toString() }


}