package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.databinding.FragmentHistoryViewBinding
import eu.vojtechh.takeyourpill.viewmodel.HistoryViewViewModel
import timber.log.Timber

@AndroidEntryPoint
class FragmentHistoryView :
    RoundedBottomSheetDialogFragment() {
    private lateinit var binding: FragmentHistoryViewBinding
    private val args: FragmentHistoryViewArgs by navArgs()
    private val model: HistoryViewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.getPillById(args.pillId).observe(viewLifecycleOwner, {
            if (it != null) {
                binding.pill = it
                initViews()
            }
        })

    }

    private fun initViews() {

        model.getHistoryForPill(args.pillId).observe(viewLifecycleOwner, {
            if (it != null) {
                Timber.e(it.toString())
            }
        })
    }
}