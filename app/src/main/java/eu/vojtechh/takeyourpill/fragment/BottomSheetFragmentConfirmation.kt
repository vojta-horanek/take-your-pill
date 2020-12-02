package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eu.vojtechh.takeyourpill.databinding.FragmentBottomConfirmBinding

class BottomSheetFragmentConfirmation :
    BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomConfirmBinding

    companion object {
        fun newInstance(
            title: String,
            confirm_text: String,
            cancel_text: String,
            confirm_icon: Int,
            cancel_icon: Int,
        ): BottomSheetFragmentConfirmation {
            val args = Bundle()
            args.putString("title", title)
            args.putString("confirm_text", confirm_text)
            args.putString("cancel_text", cancel_text)
            args.putInt("confirm_icon", confirm_icon)
            args.putInt("cancel_icon", cancel_icon)

            val bottomSheetFragmentConfirmation = BottomSheetFragmentConfirmation()
            bottomSheetFragmentConfirmation.arguments = args
            return bottomSheetFragmentConfirmation
        }
    }

    private var listener: ConfirmListener? = null

    fun setListener(listener: ConfirmListener): BottomSheetFragmentConfirmation {
        this.listener = listener
        return this
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomConfirmBinding.inflate(inflater, container, false)
        binding.title = arguments?.getString("title")
        binding.yes = arguments?.getString("confirm_text")
        binding.no = arguments?.getString("cancel_text")
        binding.listener = listener
        binding.textConfirm.setCompoundDrawablesRelativeWithIntrinsicBounds(
            arguments?.getInt("confirm_icon") ?: 0, 0, 0, 0
        )
        binding.textCancel.setCompoundDrawablesRelativeWithIntrinsicBounds(
            arguments?.getInt("cancel_icon") ?: 0, 0, 0, 0
        )
        return binding.root
    }

    interface ConfirmListener {
        fun onConfirmClicked(view: View)
        fun onCancelClicked(view: View)
    }
}