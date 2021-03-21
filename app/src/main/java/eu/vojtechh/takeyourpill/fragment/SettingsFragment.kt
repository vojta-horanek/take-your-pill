package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

}