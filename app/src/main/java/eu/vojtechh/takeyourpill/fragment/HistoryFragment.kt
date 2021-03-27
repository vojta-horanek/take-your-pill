package eu.vojtechh.takeyourpill.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialFadeThrough
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.adapter.HistoryViewPagerAdapter
import eu.vojtechh.takeyourpill.databinding.FragmentHistoryBinding

@AndroidEntryPoint
class HistoryFragment : Fragment(R.layout.fragment_history) {

    private val binding by viewBinding(FragmentHistoryBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pager.adapter = HistoryViewPagerAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.overview)
                1 -> getString(R.string.charts)
                else -> getString(R.string.history)
            }
            tab.icon = ResourcesCompat.getDrawable(
                resources, when (position) {
                    0 -> R.drawable.ic_list_alt
                    1 -> R.drawable.ic_pie_chart
                    else -> R.drawable.ic_history
                }, context?.theme
            )
        }.attach()

    }

    fun disableTabs() = binding.run {
        pager.isUserInputEnabled = false // Disable sliding
        tabLayout.isVisible = false // Hide tabs
        historyHeaderLayout.elevation = 0F // Hide elevation
    }

}