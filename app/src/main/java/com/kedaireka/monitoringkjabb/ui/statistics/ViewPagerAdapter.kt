package com.kedaireka.monitoringkjabb.ui.statistics

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kedaireka.monitoringkjabb.ui.statistics.parameter.AmmoniaFragment
import com.kedaireka.monitoringkjabb.ui.statistics.parameter.RaindropsFragment
import com.kedaireka.monitoringkjabb.ui.statistics.parameter.WaterTemperatureFragment


private const val NUM_TABS = 3

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return AmmoniaFragment()
            1 -> return RaindropsFragment()
        }

        return WaterTemperatureFragment()
    }
}