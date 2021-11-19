package com.kedaireka.monitoringkjabb.ui.statistics.parameter

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.Timestamp
import com.kedaireka.monitoringkjabb.R
import com.kedaireka.monitoringkjabb.databinding.FragmentDOBinding
import com.kedaireka.monitoringkjabb.model.Sensor
import com.kedaireka.monitoringkjabb.utils.ExcelUtils
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class DOFragment : Fragment() {

    private lateinit var doFragmentViewModel: DOFragmentViewModel
    private lateinit var allRecords: ArrayList<Sensor>

    private var _binding: FragmentDOBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        doFragmentViewModel = ViewModelProvider(this)[DOFragmentViewModel::class.java]

        _binding = FragmentDOBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sensor = getLatestSensor()
        doFragmentViewModel.getDORecord(sensor)
        doFragmentViewModel.getAllDORecord(sensor)
        doFragmentViewModel.allRecord.observe(viewLifecycleOwner, { result ->
            allRecords = result
        })

        doFragmentViewModel.records.observe(viewLifecycleOwner, { result ->
            val lineChart = binding.lineChart
            setDOLineChart(lineChart, result)
        })

        doFragmentViewModel.isLoading.observe(viewLifecycleOwner, {
            if (it) {
                binding.pbLoading.visibility = View.VISIBLE
                binding.lineChart.visibility = View.INVISIBLE
            } else {
                binding.pbLoading.visibility = View.GONE
                binding.lineChart.visibility = View.VISIBLE
            }
        })

        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        binding.button.setOnClickListener {
            Toast.makeText(this.requireContext(), "Saving Data", Toast.LENGTH_SHORT).show()

            executor.execute {
                val workbook = ExcelUtils.createWorkbook(allRecords)
                ExcelUtils.createExcel(
                    this.requireContext().applicationContext,
                    workbook,
                    sensor
                )

                handler.post {
                    Toast.makeText(this.requireContext(), "Data Saved", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return root
    }

    private fun getLatestSensor(): Sensor {
        var sensor: Sensor

        val id = "sensor_do"
        val name = "Dissolve Oxygen"
        val value = "6.3"
        val unit = "mg/l"
        val status = 0
        val createdAt = Timestamp(Date())
        val iconUrl = "url"

        sensor = Sensor(id, name, value, unit, status, createdAt, iconUrl)
        return sensor
    }

    private fun setDOLineChart(lineChart: LineChart, records: ArrayList<Sensor>) {

        val xValue = ArrayList<String>()
        val lineEntry = ArrayList<Entry>()
        val size = records.size

        for (i in 0 until size) {
            val df = DateFormat.format("ha", records[size - i - 1].created_at.toDate())

            xValue.add(df.toString())
            lineEntry.add(Entry(i.toFloat(), records[size - i - 1].value.toFloat()))
        }

        val lineDataSet = LineDataSet(lineEntry, records[0].name)
        lineDataSet.circleColors =
            mutableListOf(
                ContextCompat.getColor(
                    this.requireContext().applicationContext,
                    R.color.grey_light
                )
            )
        when (records[0].status) {
            0 -> {
                lineDataSet.color = ContextCompat.getColor(
                    this.requireContext().applicationContext,
                    R.color.blue_primary
                )
            }
            1 -> {
                lineDataSet.color =
                    ContextCompat.getColor(this.requireContext().applicationContext, R.color.yellow)
            }
            else -> {
                lineDataSet.color =
                    ContextCompat.getColor(this.requireContext().applicationContext, R.color.red)
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