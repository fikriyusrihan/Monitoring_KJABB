package com.kedaireka.monitoringkjabb.ui.detail

import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
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
    private lateinit var detailSensorViewModel: DetailSensorViewModel

    private lateinit var tvTitle: TextView
    private lateinit var tvValue: TextView
    private lateinit var tvStatus: TextView
    private lateinit var lineChart: LineChart
    private lateinit var banner: LinearLayout

    private lateinit var records: ArrayList<Sensor>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        detailSensorViewModel = ViewModelProvider(this)[DetailSensorViewModel::class.java]

        binding = ActivityDetailSensorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tvTitle = binding.tvTitle
        tvValue = binding.tvValue
        tvStatus = binding.tvStatus
        banner = binding.banner
        lineChart = binding.lineChart

        val data: Sensor = intent.extras?.get("data") as Sensor
        setData(data)

        detailSensorViewModel.getSensorRecords(data)

        detailSensorViewModel.dataSensor.observe(this, {
            records = it
            setDOLineChart(lineChart, records)
        })

        detailSensorViewModel.isLoading.observe(this, {
            if (it) {
                lineChart.visibility = View.INVISIBLE
            } else {
                lineChart.visibility = View.VISIBLE
            }
        })

        val btnBack = binding.btnBack
        btnBack.setOnClickListener {
            finish()
        }

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

    private fun setDOLineChart(lineChart: LineChart, records: ArrayList<Sensor>) {
        
        val xValue = ArrayList<String>()
        val lineEntry = ArrayList<Entry>()

        for (i in 0 until records.size) {
            val df = DateFormat.format("hha", records[i].created_at.toDate())

            xValue.add(df.toString())
            lineEntry.add(Entry(i.toFloat(), records[i].value.toFloat()))
        }

        val lineDataSet = LineDataSet(lineEntry, records[0].name)
        lineDataSet.circleColors =
            mutableListOf(ContextCompat.getColor(applicationContext, R.color.grey_light))
        when (records[0].status) {
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