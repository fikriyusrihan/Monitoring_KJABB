package com.kedaireka.monitoringkjabb.ui.statistics.parameter

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import com.kedaireka.monitoringkjabb.R
import com.kedaireka.monitoringkjabb.databinding.FragmentRaindropsBinding
import com.kedaireka.monitoringkjabb.model.Sensor
import com.kedaireka.monitoringkjabb.ui.detail.DetailSensorActivity
import com.kedaireka.monitoringkjabb.utils.ExcelUtils
import com.kedaireka.monitoringkjabb.utils.RaindropsMapper.Companion.RAINDROPS_DICT
import java.util.*
import java.util.concurrent.Executors

class RaindropsFragment : Fragment() {

    private lateinit var raindropsFragmentViewModel: RaindropsFragmentViewModel
    private lateinit var recordsInRange: ArrayList<Sensor>

    private var _binding: FragmentRaindropsBinding? = null
    private val binding get() = _binding!!

    private var max = 0.0
    private var min = 0.0
    private var avg = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        raindropsFragmentViewModel = ViewModelProvider(this)[RaindropsFragmentViewModel::class.java]

        _binding = FragmentRaindropsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sensor = getLatestSensor()
        raindropsFragmentViewModel.getDORecord(sensor)
        raindropsFragmentViewModel.getThresholdsData(sensor)

        raindropsFragmentViewModel.avg.observe(viewLifecycleOwner, { result ->
            avg = result
            val value = getString(RAINDROPS_DICT[avg.toInt()]!!)
            binding.tvAvg.text = value
        })

        raindropsFragmentViewModel.max.observe(viewLifecycleOwner, {
            max = it
            val value =
                "Max: ${getString(RAINDROPS_DICT[max.toInt()]!!)} | Min: ${getString(RAINDROPS_DICT[min.toInt()]!!)}"
            binding.tvMaxMin.text = value
        })

        raindropsFragmentViewModel.min.observe(viewLifecycleOwner, {
            min = it
            val value =
                "Max: ${getString(RAINDROPS_DICT[max.toInt()]!!)} | Min: ${getString(RAINDROPS_DICT[min.toInt()]!!)}"
            binding.tvMaxMin.text = value
        })

        raindropsFragmentViewModel.records.observe(viewLifecycleOwner, { result ->
            val lineChart = binding.lineChart
            setDOLineChart(lineChart, result)
        })

        raindropsFragmentViewModel.thresholds.observe(viewLifecycleOwner, {
            val upper = it["upper"]?.toDouble()!!
            val lower = it["lower"]?.toDouble()!!

            if (avg in lower..upper) {
                binding.tvStatus.text = getString(R.string.status_good)
            } else {
                binding.tvStatus.text = getString(R.string.status_bad)
                binding.cardStatus.setCardBackgroundColor(resources.getColor(R.color.yellow))
            }
        })

        raindropsFragmentViewModel.isLoading.observe(viewLifecycleOwner, {
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
            // Check storage permission before download
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                checkPermission(
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    DetailSensorActivity.MANAGE_STORAGE_PERMISSION_CODE
                )

                if (!Environment.isExternalStorageManager()) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                    val uri = Uri.fromParts("package", requireContext().packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }

            } else {
                checkPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    DetailSensorActivity.STORAGE_PERMISSION_CODE
                )
            }

            val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select dates")
                .setSelection(
                    Pair(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                    )
                )
                .build()

            dateRangePicker.addOnPositiveButtonClickListener { time ->
                // Generate Data
                Toast.makeText(requireContext(), "Saving Data", Toast.LENGTH_SHORT).show()

                raindropsFragmentViewModel.getSensorRecordInRange(
                    sensor,
                    time.first / 1000,
                    time.second / 1000
                )
                raindropsFragmentViewModel.sensorRecordInRange.observe(requireActivity(), {
                    recordsInRange = it

                    if (recordsInRange.isNotEmpty()) {
                        executor.execute {
                            val workbook = ExcelUtils.createWorkbook(recordsInRange)
                            ExcelUtils.createExcel(
                                requireContext().applicationContext,
                                workbook,
                                sensor
                            )

                            handler.post {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.data_saved),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.saving_failed),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                })
            }

            dateRangePicker.show(requireActivity().supportFragmentManager, "DetailSensorActivity")
        }

        return root
    }

    private fun getLatestSensor(): Sensor {
        val sensor: Sensor

        val id = "raindrops"
        val name = "Raindrops"
        val value = "6.3"
        val unit = ""
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

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            // Requesting permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(permission),
                requestCode
            )
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.permission_already_granted),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}