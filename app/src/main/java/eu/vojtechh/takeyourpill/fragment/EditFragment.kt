package eu.vojtechh.takeyourpill.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.FragmentEditBinding
import eu.vojtechh.takeyourpill.klass.themeColor
import eu.vojtechh.takeyourpill.viewmodel.EditViewModel

@AndroidEntryPoint
class EditFragment : Fragment() {

    private val model: EditViewModel by viewModels()
    private val args: EditFragmentArgs by navArgs()

    private lateinit var binding: FragmentEditBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater, container, false)
        if (args.pillId == -1L) {
            enterTransition = MaterialContainerTransform().apply {
                startView = requireActivity().findViewById(R.id.floatingActionButton)
                endView = requireActivity().findViewById(R.id.editView)
                scrimColor = Color.TRANSPARENT
                containerColor = requireContext().themeColor(R.attr.colorSurface)
                startContainerColor = requireContext().themeColor(R.attr.colorSecondary)
                endContainerColor = requireContext().themeColor(R.attr.colorSurface)
            }
            returnTransition = Slide().apply {
                addTarget(R.id.editView)
            }
        } else {
            enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
            returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Opened with existing pill
        if (args.pillId != -1L) {
            binding.textNewPill.text = getString(R.string.edit_pill)
            postponeEnterTransition()
            if (model.pill == null) {
                model.getPillById(args.pillId).observe(viewLifecycleOwner, {
                    model.pill = it
                    binding.pill = model.pill
                    startPostponedEnterTransition()
                })
            } else {
                binding.pill = model.pill
                startPostponedEnterTransition()
            }
        } // Opened with new pill
        else {
            binding.textNewPill.text = getString(R.string.new_pill)
            if (model.pill == null) {
                model.pill = model.getNewEmptyPill()
            }
            binding.pill = model.pill
        }

        binding.inputName.doOnTextChanged { text, _, _, _ ->
            binding.inputNameLayout.error =
                if (text.isNullOrBlank()) getString(R.string.enter_field) else null
            text?.let { model.pill?.name = it.trim().toString() }
        }

        binding.buttonSave.setOnClickListener {
            if (model.pill?.name.isNullOrBlank()) {
                binding.inputNameLayout.error = getString(R.string.enter_field)
                return@setOnClickListener
            }
            returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
            if (args.pillId == -1L) {
                model.addPill(model.pill!!).observe(viewLifecycleOwner) {
                    findNavController().popBackStack()
                    val directions = HomeFragmentDirections.actionHomescreenToDetails(it, true)
                    findNavController().navigate(directions)
                }
            } else {
                model.pill!!.apply {
                    name = binding.inputName.text.toString()
                    description = binding.inputDescription.text.toString()
                    // TODO
                }
                model.updatePill(model.pill!!)
                findNavController().popBackStack()
            }
        }
    }
}