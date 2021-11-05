package com.kedaireka.monitoringkjabb.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.kedaireka.monitoringkjabb.R
import com.kedaireka.monitoringkjabb.databinding.ActivityDetailSensorBinding

class DetailSensorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailSensorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailSensorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnBack = binding.btnBack
        btnBack.setOnClickListener {
            finish()
        }

        val lineChart = binding.lineChart
        setDOLineChart(lineChart)
    }

    private fun setDOLineChart(lineChart: LineChart) {
        val xValue = ArrayList<String>()
        xValue.add("7am")
        xValue.add("10am")
        xValue.add("1pm")
        xValue.add("4pm")
        xValue.add("7pm")
        xValue.add("10pm")

        val lineEntry = ArrayList<Entry>()
        lineEntry.add(Entry(0F, 6.2F))
        lineEntry.add(Entry(1F, 7.1F))
        lineEntry.add(Entry(2F, 6.5F))
        lineEntry.add(Entry(3F, 6.8F))
        lineEntry.add(Entry(4F, 7.4F))
        lineEntry.add(Entry(5F, 6.9F))

        val lineDataSet = LineDataSet(lineEntry, resources.getString(R.string.dissolved_oxygen))
        lineDataSet.color = resources.getColor(R.color.blue_primary)

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setLabelCount(xValue.size, true)
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return xValue[value.toInt()]
            }
        }

        val data = LineData(lineDataSet)
        lineChart.data = data
        lineChart.setScaleEnabled(false)

    }
}