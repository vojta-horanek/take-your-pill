package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import eu.vojtechh.takeyourpill.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}