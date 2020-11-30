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
import eu.vojtechh.takeyourpill.databinding.FragmentDetailsBinding
import eu.vojtechh.takeyourpill.klass.themeColor
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.viewmodel.DetailsViewModel

@AndroidEntryPoint
class DetailsFragment : Fragment(),
    BottomSheetFragmentConfirmation.ConfirmListener {

    private val model: DetailsViewModel by viewModels()
    private val args: DetailsFragmentArgs by navArgs()
    private lateinit var binding: FragmentDetailsBinding
    private lateinit var pill: Pill
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.navHostFragment
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }

        binding = FragmentDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        if (args.saved) {
            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
        }

        model.getPillById(args.pillId).observe(viewLifecycleOwner, {
            pill = it
            binding.pill = pill
            startPostponedEnterTransition()
        })

        binding.buttonDelete.setOnClickListener {
            val confirmSheet = BottomSheetFragmentConfirmation.newInstance(
                getString(R.string.confirm_delete_pill),
                getString(R.string.delete),
                getString(R.string.cancel),
                R.drawable.ic_delete,
                R.drawable.ic_cancel,
            )
                .setListener(this) // TODO the listener get deleted when BottomS.. open and theme is changed
            confirmSheet.show(childFragmentManager, "confirm_delete")
        }

        binding.buttonEdit.setOnClickListener {
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
            val directions = DetailsFragmentDirections.actionDetailsFragmentToEditFragment(pill.id)
            findNavController().navigate(directions)
        }

    }

    override fun onConfirmClicked(view: View) {
        model.deletePill(pill)
        findNavController().navigateUp()
    }

    override fun onCancelClicked(view: View) {
        (childFragmentManager.findFragmentByTag("confirm_delete") as BottomSheetFragmentConfirmation).dismiss()
    }
}