package eu.vojtechh.takeyourpill.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import eu.vojtechh.takeyourpill.fragment.HistoryChartFragment
import eu.vojtechh.takeyourpill.fragment.HistoryOverviewFragment

class HistoryViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HistoryOverviewFragment()
            1 -> HistoryChartFragment()
            else -> throw IllegalStateException("How did you get here?")
        }
    }
}