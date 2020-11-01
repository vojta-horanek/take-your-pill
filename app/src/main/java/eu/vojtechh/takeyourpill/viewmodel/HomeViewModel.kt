package eu.vojtechh.takeyourpill.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val pillsRemainingToday: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().also {
            loadPillsRemainingToday()
        }
    }

    fun getPillsRemainingToday(): LiveData<Int> {
        return pillsRemainingToday
    }

    private fun loadPillsRemainingToday(): Int {
        return 1
        //TODO Use database from Android Components
    }
}