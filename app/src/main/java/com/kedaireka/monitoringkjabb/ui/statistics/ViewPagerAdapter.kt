package com.kedaireka.monitoringkjabb.ui.statistics

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kedaireka.monitoringkjabb.ui.statistics.parameter.DOFragment
import com.kedaireka.monitoringkjabb.ui.statistics.parameter.PHFragment
import com.kedaireka.monitoringkjabb.ui.statistics.parameter.SalinityFragment
import com.kedaireka.monitoringkjabb.ui.statistics.parameter.WaterTemperatureFragment


private const val NUM_TABS = 4

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return DOFragment()
            1 -> return PHFragment()
            2 -> return WaterTemperatureFragment()
        }

        return SalinityFragment()
    }
}