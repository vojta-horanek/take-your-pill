package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.DetailsFragmentBinding
import eu.vojtechh.takeyourpill.klass.viewBinding
import eu.vojtechh.takeyourpill.viewmodel.DetailsViewModel

class DetailsFragment : Fragment(R.layout.details_fragment) {

    private val model: DetailsViewModel by viewModels()
    private val view by viewBinding(DetailsFragmentBinding::bind)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}