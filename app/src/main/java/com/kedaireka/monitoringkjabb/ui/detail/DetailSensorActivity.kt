package com.kedaireka.monitoringkjabb.ui.detail

import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kedaireka.monitoringkjabb.R
import com.kedaireka.monitoringkjabb.databinding.ActivityDetailSensorBinding
import com.kedaireka.monitoringkjabb.model.Sensor

class DetailSensorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailSensorBinding
    private lateinit var detailSensorViewModel: DetailSensorViewModel

    private lateinit var tvTitle: TextView
    private lateinit var tvValue: TextView
    private lateinit var tvStatus: TextView
    private lateinit var pbDetail: ProgressBar
    private lateinit var lineChart: LineChart
    private lateinit var banner: LinearLayout
    private lateinit var thresholdStatus: TextView

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
        pbDetail = binding.pbDetail
        lineChart = binding.lineChart
        thresholdStatus = binding.tvThresholdsStatus

        val data: Sensor = intent.extras?.get("data") as Sensor
        setData(data)

        detailSensorViewModel.getSensorRecords(data)
        detailSensorViewModel.getThresholdsData(data)

        detailSensorViewModel.dataSensor.observe(this, {
            records = it
            setDOLineChart(lineChart, records)
        })

        detailSensorViewModel.isLoading.observe(this, {
            if (it) {
                pbDetail.visibility = View.VISIBLE
                lineChart.visibility = View.INVISIBLE
            } else {
                pbDetail.visibility = View.GONE
                lineChart.visibility = View.VISIBLE
            }
        })

        detailSensorViewModel.thresholds.observe(this, { result ->
            val thresholdLower = result["lower"].toString()
            val thresholdUpper = result["upper"].toString()

            setThresholdStatus(thresholdUpper, thresholdLower, data)
        })

        val btnBack = binding.btnBack
        btnBack.setOnClickListener {
            finish()
        }

        val btnSetThreshold = binding.cvThresholdSetting
        btnSetThreshold.setOnClickListener {

            val formView = layoutInflater.inflate(R.layout.setting_threshold, null, false)
            val edtLowerLimit = formView.findViewById<TextInputLayout>(R.id.et_threshold_lower)
            val edtUpperLimit = formView.findViewById<TextInputLayout>(R.id.et_threshold_upper)

            edtLowerLimit.suffixText = data.unit
            edtUpperLimit.suffixText = data.unit

            MaterialAlertDialogBuilder(this)
                .setView(formView)
                .setTitle("Setting Threshold")
                .setNegativeButton("Cancel") { _, _ ->
                    val toast =
                        Toast.makeText(this, "Data Not Change", Toast.LENGTH_SHORT).show()
                }
                .setPositiveButton("Set") { _, _ ->
                    val upperValue = edtUpperLimit.editText?.text.toString()
                    val lowerValue = edtLowerLimit.editText?.text.toString()

                    val threshold = hashMapOf(
                        "upper" to upperValue,
                        "lower" to lowerValue,
                    )

                    Firebase.firestore.collection("sensors").document(data.id)
                        .collection("thresholds").document("data")
                        .set(threshold)
                        .addOnSuccessListener {
                            val toast =
                                Toast.makeText(this, "Data Saved", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            val toast =
                                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                        }

                    setThresholdStatus(upperValue, lowerValue, data)
                }
                .show()
        }

        val btnDownload = binding.cvDownloadData
        btnDownload.setOnClickListener {
            val toast = Toast.makeText(this, "Downloading data", Toast.LENGTH_SHORT)
            toast.show()
        }

    }

    private fun setData(sensor: Sensor) {
        val displayValue = "${sensor.value} ${sensor.unit}"
        tvTitle.text = sensor.name
        tvValue.text = displayValue
        parseStatus(sensor.status)

    }

    private fun setThresholdStatus(upper: String, lower: String, sensor: Sensor) {
        val text = "$lower - $upper ${sensor.unit}"
        thresholdStatus.text = text
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
        val size = records.size

        for (i in 0 until size) {
            val df = DateFormat.format("ha", records[size - i - 1].created_at.toDate())

            xValue.add(df.toString())
            lineEntry.add(Entry(i.toFloat(), records[size - i - 1].value.toFloat()))
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