package com.kedaireka.monitoringkjabb.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kedaireka.monitoringkjabb.model.Sensor
import com.kedaireka.monitoringkjabb.ui.history.parameter.DailyHistoryFragment
import com.kedaireka.monitoringkjabb.ui.history.parameter.MonthlyHistroryFragment
import com.kedaireka.monitoringkjabb.ui.history.parameter.WeeklyHistoryFragment

private const val NUM_TABS = 3

class HistoryViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val sensor: Sensor
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                val bundle = Bundle()
                val dailyHistoryFragment = DailyHistoryFragment()
                bundle.putParcelable("data", sensor)
                dailyHistoryFragment.arguments = bundle

                return dailyHistoryFragment
            }
            1 -> {
                val bundle = Bundle()
                val weeklyHistoryFragment = WeeklyHistoryFragment()
                bundle.putParcelable("data", sensor)
                weeklyHistoryFragment.arguments = bundle

                return weeklyHistoryFragment
            }
        }

        val bundle = Bundle()
        val monthlyHistroryFragment = MonthlyHistroryFragment()
        bundle.putParcelable("data", sensor)
        monthlyHistroryFragment.arguments = bundle
        return monthlyHistroryFragment
    }
}