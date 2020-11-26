package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eu.vojtechh.takeyourpill.databinding.FragmentBottomConfirmBinding

class BottomSheetFragmentConfirmation(
    private val title: String,
    private val confirm_text: String,
    private val cancel_text: String,
    private val listener: ConfirmListener
) :
    BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomConfirmBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomConfirmBinding.inflate(inflater, container, false)
        binding.title = title
        binding.yes = confirm_text
        binding.no = cancel_text
        binding.listener = listener
        return binding.root
    }

    interface ConfirmListener {
        fun onConfirmClicked(view: View)
        fun onCancelClicked(view: View)
    }
}