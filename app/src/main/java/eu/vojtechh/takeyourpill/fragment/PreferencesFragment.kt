package eu.vojtechh.takeyourpill.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.OVER_SCROLL_NEVER
import androidx.fragment.app.viewModels
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.activity.AboutActivity
import eu.vojtechh.takeyourpill.klass.Pref
import eu.vojtechh.takeyourpill.klass.Utils
import eu.vojtechh.takeyourpill.viewmodel.PreferencesViewModel

@AndroidEntryPoint
class PreferencesFragment : PreferenceFragmentCompat() {

    private val model: PreferencesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.apply {
            clipToPadding = false
            overScrollMode = OVER_SCROLL_NEVER
            setPadding(
                0,
                0,
                0,
                view.context.resources.getDimension(R.dimen.list_with_navigation_padding).toInt()
            )
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // If we are running on older than oreo, set summary to represent the available options in android settings
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            findPreference<Preference>("notificationOptions")?.summary =
                getString(R.string.settings_notification_options_summary_legacy)
        }
        findPreference<Preference>("notificationOptions")?.setOnPreferenceClickListener {
            val intent = Intent()
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"

            intent.putExtra("app_package", requireContext().packageName)
            intent.putExtra("app_uid", requireContext().applicationInfo.uid)
            intent.putExtra("android.provider.extra.APP_PACKAGE", requireContext().packageName)

            startActivity(intent)
            true
        }

        findPreference<Preference>("showAbout")?.setOnPreferenceClickListener {
            val intent = Intent(requireActivity(), AboutActivity::class.java)
            startActivity(intent)
            true
        }

        // Uncomment after dlouhodobka
        // findPreference<Preference>("addTestData")?.isVisible = BuildConfig.DEBUG

        findPreference<Preference>("addTestData")?.setOnPreferenceClickListener {
            model.addTestData(requireContext())
            true
        }

        findPreference<ListPreference>("themeKey")?.setOnPreferenceChangeListener { _, newValue ->
            Pref.theme = newValue.toString() // Doesn't save?
            Utils.setTheme(Pref.theme)
            true
        }

    }
}