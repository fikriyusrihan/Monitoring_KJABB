package com.kedaireka.monitoringkjabb.ui.history

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.kedaireka.monitoringkjabb.databinding.ActivityHistorySensorBinding
import com.kedaireka.monitoringkjabb.model.Sensor

private val parameterArray = arrayOf(
    "Today",
    "7 Days",
    "31 Days"
)

class HistorySensorActivity : AppCompatActivity() {

    private lateinit var historySensorBinding: ActivityHistorySensorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        historySensorBinding = ActivityHistorySensorBinding.inflate(layoutInflater)
        setContentView(historySensorBinding.root)

        val sensor = intent.extras?.get("data") as Sensor


        val viewPager = historySensorBinding.historyViewpager
        val tabLayout = historySensorBinding.historyTablayout

        val adapter = HistoryViewPagerAdapter(supportFragmentManager, lifecycle, sensor)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = parameterArray[position]
        }.attach()


        historySensorBinding.btnBack.setOnClickListener {
            finish()
        }
    }
}