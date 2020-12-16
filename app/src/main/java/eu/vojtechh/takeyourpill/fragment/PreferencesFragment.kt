package eu.vojtechh.takeyourpill.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OVER_SCROLL_NEVER
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.view.updateLayoutParams
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.activity.AboutActivity

class PreferencesFragment : PreferenceFragmentCompat() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.updateLayoutParams { height = WRAP_CONTENT }
        listView.clipToPadding = false
        listView.overScrollMode = OVER_SCROLL_NEVER
        listView.setPadding(
            0,
            0,
            0,
            view.context.resources.getDimension(R.dimen.list_with_navigation_padding).toInt()
        )
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        findPreference<Preference>("notification_options")?.setOnPreferenceClickListener {
            val intent = Intent()
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"

            intent.putExtra("app_package", requireContext().packageName)
            intent.putExtra("app_uid", requireContext().applicationInfo.uid)
            intent.putExtra("android.provider.extra.APP_PACKAGE", requireContext().packageName)

            startActivity(intent)
            false
        }

        findPreference<Preference>("show_about")?.setOnPreferenceClickListener {
            val intent = Intent(requireActivity(), AboutActivity::class.java)
            startActivity(intent)
            false
        }
    }
}