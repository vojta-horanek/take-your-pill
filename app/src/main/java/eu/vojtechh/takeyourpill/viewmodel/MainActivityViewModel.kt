package eu.vojtechh.takeyourpill.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    private val pillsUnread: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().also {
            loadUnreadPills()
        }
    }

    fun getUsers(): LiveData<Int> {
        return pillsUnread
    }

    private fun loadUnreadPills(): Int {
        return 1
        //TODO Use database from Android Components
    }
}