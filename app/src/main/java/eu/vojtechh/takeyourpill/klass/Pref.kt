package eu.vojtechh.takeyourpill.klass

import com.marcinmoskala.kotlinpreferences.PreferenceHolder

object Pref : PreferenceHolder() {
    var alertStyle: Boolean by bindToPreferenceField(false)
    var buttonDelay: Int by bindToPreferenceField(30)
    var remindAgain: Boolean by bindToPreferenceField(true)
    var remindAgainAfter: Int by bindToPreferenceField(10)
    var firstRun: Boolean by bindToPreferenceField(true)
    var theme: String by bindToPreferenceField("0")
}