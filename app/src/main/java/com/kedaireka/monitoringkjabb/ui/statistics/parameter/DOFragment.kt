package com.kedaireka.monitoringkjabb.ui.statistics.parameter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.kedaireka.monitoringkjabb.R
import com.kedaireka.monitoringkjabb.databinding.FragmentDOBinding

class DOFragment : Fragment() {

    private var _binding: FragmentDOBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDOBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val lineChart = binding.lineChart
        setDOLineChart(lineChart)

        return root
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