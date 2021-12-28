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
import com.kedaireka.monitoringkjabb.databinding.FragmentAmmoniaBinding
import com.kedaireka.monitoringkjabb.model.Sensor
import com.kedaireka.monitoringkjabb.utils.ExcelUtils
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class AmmoniaFragment : Fragment() {

    private lateinit var ammoniaFragmentViewModel: AmmoniaFragmentViewModel
    private lateinit var allRecords: ArrayList<Sensor>

    private var _binding: FragmentAmmoniaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ammoniaFragmentViewModel = ViewModelProvider(this)[AmmoniaFragmentViewModel::class.java]

        _binding = FragmentAmmoniaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sensor = getLatestSensor()
        ammoniaFragmentViewModel.getDORecord(sensor)
        ammoniaFragmentViewModel.getAllDORecord(sensor)
        ammoniaFragmentViewModel.allRecord.observe(viewLifecycleOwner, { result ->
            allRecords = result
        })

        ammoniaFragmentViewModel.records.observe(viewLifecycleOwner, { result ->
            val lineChart = binding.lineChart
            setDOLineChart(lineChart, result)
        })

        ammoniaFragmentViewModel.isLoading.observe(viewLifecycleOwner, {
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
        val sensor: Sensor

        val id = "sensor_do"
        val name = "Dissolve Oxygen"
        val value = "6.3"
        val unit = "mg/l"
        val createdAt = Timestamp(Date())
        val iconUrl = "url"

        sensor = Sensor(id, name, value, unit, createdAt, iconUrl)
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