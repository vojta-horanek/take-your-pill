package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.transition.MaterialSharedAxis
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.FragmentAboutBinding
import eu.vojtechh.takeyourpill.klass.viewBinding

class AboutFragment : Fragment(R.layout.fragment_about) {

    private val model: AboutFragment by viewModels()
    private val view by viewBinding(FragmentAboutBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}