package eu.vojtechh.takeyourpill.klass

import androidx.appcompat.app.AppCompatDelegate

class Utils {
    companion object {
        fun setTheme(theme: String) {
            val auto = theme == "0"
            val dark = theme == "2"
            when (auto) {
                true -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
                false -> {
                    when (dark) {
                        true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
            }
        }
    }
}