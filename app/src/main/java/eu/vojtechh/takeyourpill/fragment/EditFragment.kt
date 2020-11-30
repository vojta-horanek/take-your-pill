package eu.vojtechh.takeyourpill.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.FragmentEditBinding
import eu.vojtechh.takeyourpill.klass.themeColor
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.model.PillColor
import eu.vojtechh.takeyourpill.reminder.ReminderOptions
import eu.vojtechh.takeyourpill.viewmodel.EditViewModel

@AndroidEntryPoint
class EditFragment : Fragment() {

    private val model: EditViewModel by viewModels()
    private val args: EditFragmentArgs by navArgs()

    private lateinit var binding: FragmentEditBinding
    private lateinit var pill: Pill
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater, container, false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
        if (args.pillId == -1L) {
            enterTransition = MaterialContainerTransform().apply {
                startView = requireActivity().findViewById(R.id.floatingActionButton)
                endView = requireActivity().findViewById(R.id.editView)
                scrimColor = Color.TRANSPARENT
                containerColor = requireContext().themeColor(R.attr.colorSurface)
                startContainerColor = requireContext().themeColor(R.attr.colorSecondary)
                endContainerColor = requireContext().themeColor(R.attr.colorSurface)
            }
        } else {
            enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()

        if (args.pillId != -1L) {
            model.getPillById(args.pillId).observe(viewLifecycleOwner, {
                pill = it
                binding.pill = pill
                startPostponedEnterTransition()
            })
        }

        binding.buttonSave.setOnClickListener {

            if (args.pillId == -1L) {
                val newPill = Pill(
                    binding.inputName.text.toString(),
                    binding.inputDescription.text.toString(),
                    null,
                    PillColor(R.color.colorDarkBlue),
                    ReminderOptions.Infinite(mutableListOf()),
                    ReminderOptions.Infinite(mutableListOf())
                )
                model.addPill(newPill).observe(viewLifecycleOwner) {
                    findNavController().popBackStack()
                    val directions = HomeFragmentDirections.actionHomescreenToDetails(it, true)
                    findNavController().navigate(directions)
                }
            } else {
                pill.apply {
                    name = binding.inputName.text.toString()
                    description = binding.inputDescription.text.toString()
                    // TODO
                }
                model.updatePill(pill)
                findNavController().popBackStack()
            }
        }
    }
}