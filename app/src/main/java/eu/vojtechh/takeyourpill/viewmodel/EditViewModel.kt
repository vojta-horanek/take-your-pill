package eu.vojtechh.takeyourpill.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.PillColor
import eu.vojtechh.takeyourpill.model.Reminder
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.InputStream

class EditViewModel @ViewModelInject constructor(
    private val pillRepository: PillRepository
) : ViewModel() {
    fun addPill(pill: Pill) = liveData { emit(pillRepository.insertPill(pill)) }
    fun updatePill(pill: Pill) = liveData { emit(pillRepository.updatePill(pill)) }

    fun getPillById(pillId: Long) = pillRepository.getPill(pillId)

    fun getNewEmptyPill() = Pill.getEmpty()

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
        if (reminder.reminderId != 0L) {
            newList?.removeAll { r -> r.reminderId == reminder.reminderId }
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

    fun setImage(data: Uri, context: Context) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val inputStream: InputStream? =
                context.contentResolver.openInputStream(data)
            // FIXME Fix rotation
            val userBitmap = BitmapFactory.decodeStream(inputStream)
            val scaledBitmap = Bitmap.createScaledBitmap(
                userBitmap,
                (userBitmap.width.toFloat() * 0.8).toInt(), // Downscale image
                (userBitmap.height.toFloat() * 0.8).toInt(),
                false
            )
            _photoBitmap.postValue(scaledBitmap)
        } catch (e: FileNotFoundException) {
            // Ignore
        }
    }

    fun deleteImage() {
        _photoBitmap.value = null
    }


}