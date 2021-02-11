package eu.vojtechh.takeyourpill.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import eu.vojtechh.takeyourpill.fragment.HistoryChartFragment
import eu.vojtechh.takeyourpill.fragment.HistoryOverviewFragment
import eu.vojtechh.takeyourpill.fragment.HistoryStatFragment

class HistoryViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HistoryOverviewFragment()
            1 -> HistoryStatFragment()
            2 -> HistoryChartFragment()
            else -> throw IllegalStateException("How did you get here?")
        }
    }
}