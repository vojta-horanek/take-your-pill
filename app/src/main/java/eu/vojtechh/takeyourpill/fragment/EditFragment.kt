package eu.vojtechh.takeyourpill.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.Slide
import com.google.android.material.transition.MaterialContainerTransform
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.EditFragmentBinding
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.klass.themeColor
import eu.vojtechh.takeyourpill.klass.viewBinding
import eu.vojtechh.takeyourpill.viewmodel.EditViewModel

class EditFragment : Fragment(R.layout.edit_fragment) {

    private val model: EditViewModel by viewModels()
    private val view by viewBinding(EditFragmentBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialContainerTransform().apply {
            startView = requireActivity().findViewById(R.id.floatingActionButton)
            endView = requireActivity().findViewById(R.id.editView)
            duration = Constants.ANIMATION_DURATION
            scrimColor = Color.TRANSPARENT
            containerColor = requireContext().themeColor(R.attr.colorSurface)
            startContainerColor = requireContext().themeColor(R.attr.colorSecondary)
            endContainerColor = requireContext().themeColor(R.attr.colorSurface)
        }
        returnTransition = Slide().apply {
            duration = Constants.ANIMATION_DURATION
            addTarget(R.id.editView)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}