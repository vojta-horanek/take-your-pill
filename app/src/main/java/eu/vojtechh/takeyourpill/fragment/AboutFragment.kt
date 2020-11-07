package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.AboutFragmentBinding
import eu.vojtechh.takeyourpill.klass.viewBinding

class AboutFragment : Fragment(R.layout.about_fragment) {

    private val model: AboutFragment by viewModels()
    private val view by viewBinding(AboutFragmentBinding::bind)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}