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
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.FragmentDetailsBinding
import eu.vojtechh.takeyourpill.klass.themeColor
import eu.vojtechh.takeyourpill.model.Pill
import eu.vojtechh.takeyourpill.viewmodel.DetailsViewModel

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details),
    BottomSheetFragmentConfirmation.ConfirmListener {

    private val model: DetailsViewModel by viewModels()
    val args: DetailsFragmentArgs by navArgs()
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
        // Get the pill in a blocking manner, it is only text which should take just a few seconds
        model.getPillBlocking(args.pillId)?.let {
            pill = it
        } ?: run {
            TODO("Implement pill not found")
        }
        binding.pill = pill
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.buttonDelete.setOnClickListener {
            val confirmSheet = BottomSheetFragmentConfirmation(
                getString(R.string.confirm_delete_pill),
                getString(R.string.delete),
                getString(R.string.cancel),
                this
            )
            confirmSheet.show(childFragmentManager, "confirm_delete")
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