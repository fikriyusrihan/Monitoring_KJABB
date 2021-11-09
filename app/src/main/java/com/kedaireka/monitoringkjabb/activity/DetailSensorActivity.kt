package com.kedaireka.monitoringkjabb.activity

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.kedaireka.monitoringkjabb.R
import com.kedaireka.monitoringkjabb.databinding.ActivityDetailSensorBinding
import com.kedaireka.monitoringkjabb.model.Sensor

class DetailSensorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailSensorBinding
    private lateinit var tvTitle: TextView
    private lateinit var tvValue: TextView
    private lateinit var tvStatus: TextView
    private lateinit var banner: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailSensorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tvTitle = binding.tvTitle
        tvValue = binding.tvValue
        tvStatus = binding.tvStatus
        banner = binding.banner

        val data: Sensor = intent.extras?.get("data") as Sensor
        setData(data)

        val btnBack = binding.btnBack
        btnBack.setOnClickListener {
            finish()
        }

        val lineChart = binding.lineChart
        setDOLineChart(lineChart, data)
    }

    private fun setData(sensor: Sensor) {
        val displayValue = "${sensor.value} ${sensor.unit}"
        tvTitle.text = sensor.name
        tvValue.text = displayValue
        parseStatus(sensor.status)

    }

    private fun parseStatus(status: Int) {
        when (status) {
            0 -> {
                val statusText = "Good"
                tvStatus.text = statusText
                banner.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.blue_primary
                    )
                )
            }
            1 -> {
                val statusText = "Moderate"
                tvStatus.text = statusText
                banner.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.yellow
                    )
                )
            }
            else -> {
                val statusText = "Bad"
                tvStatus.text = statusText
                banner.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.red
                    )
                )
            }
        }
    }

    private fun setDOLineChart(lineChart: LineChart, sensor: Sensor) {
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

        val lineDataSet = LineDataSet(lineEntry, sensor.name)
        lineDataSet.circleColors =
            mutableListOf(ContextCompat.getColor(applicationContext, R.color.grey_light))
        when (sensor.status) {
            0 -> {
                lineDataSet.color = ContextCompat.getColor(applicationContext, R.color.blue_primary)
            }
            1 -> {
                lineDataSet.color = ContextCompat.getColor(applicationContext, R.color.yellow)
            }
            else -> {
                lineDataSet.color = ContextCompat.getColor(applicationContext, R.color.red)
            }
        }

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