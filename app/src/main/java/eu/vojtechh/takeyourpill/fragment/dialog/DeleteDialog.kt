package eu.vojtechh.takeyourpill.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.vojtechh.takeyourpill.databinding.DialogDeleteBinding

class DeleteDialog : RoundedDialogFragment() {
    private lateinit var binding: DialogDeleteBinding

    var listener: (what: Boolean) -> Unit = {}

    fun setUserListener(listener: (Boolean) -> Unit) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDeleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonPillOnly.setOnClickListener { listener(false) }
        binding.buttonPillHistory.setOnClickListener { listener(false) }
    }
}