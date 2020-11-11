package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.transition.MaterialSharedAxis
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.HistoryFragmentBinding
import eu.vojtechh.takeyourpill.klass.viewBinding
import eu.vojtechh.takeyourpill.viewmodel.HistoryViewModel

class HistoryFragment : Fragment(R.layout.history_fragment) {

    private val model: HistoryViewModel by viewModels()
    private val view by viewBinding(HistoryFragmentBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}