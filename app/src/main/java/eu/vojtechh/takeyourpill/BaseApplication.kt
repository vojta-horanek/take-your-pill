package eu.vojtechh.takeyourpill

import android.app.Application
import com.marcinmoskala.kotlinpreferences.PreferenceHolder
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

import timber.log.Timber.DebugTree

@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        PreferenceHolder.setContext(applicationContext)
    }
}