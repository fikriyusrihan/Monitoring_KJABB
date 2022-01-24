package com.kedaireka.monitoringkjabb.ui.history.parameter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kedaireka.monitoringkjabb.adapter.ListHistoryAdapter
import com.kedaireka.monitoringkjabb.databinding.FragmentWeeklyHistoryBinding
import com.kedaireka.monitoringkjabb.model.Sensor
import com.kedaireka.monitoringkjabb.utils.RaindropsMapper

class WeeklyHistoryFragment : Fragment() {

    private lateinit var weeklyHistoryViewModel: WeeklyHistoryViewModel

    private var _binding: FragmentWeeklyHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var list: ArrayList<Sensor>
    private lateinit var rvHistory: RecyclerView

    private var min = 0.0
    private var max = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        weeklyHistoryViewModel = ViewModelProvider(this)[WeeklyHistoryViewModel::class.java]

        _binding = FragmentWeeklyHistoryBinding.inflate(inflater, container, false)

        rvHistory = binding.rvRecords
        rvHistory.setHasFixedSize(true)

        val bundle = arguments
        val sensor = bundle!!.getParcelable<Sensor>("data") as Sensor

        weeklyHistoryViewModel.getSensorHistory(sensor)

        weeklyHistoryViewModel.avg.observe(viewLifecycleOwner, {
            if (sensor.id == RaindropsMapper.RAINDROPS_ID) {
                binding.tvValue.text = RaindropsMapper.RAINDROPS_DICT[it.toInt()]
            } else {
                val value = "%.2f ${sensor.unit}".format(it)
                binding.tvValue.text = value
            }
        })

        weeklyHistoryViewModel.max.observe(viewLifecycleOwner, {
            max = it
            val value = "Max: $max | Min: $min"
            binding.tvMaxMin.text = value
        })

        weeklyHistoryViewModel.min.observe(viewLifecycleOwner, {
            min = it
            val value = "Max: $max | Min: $min"
            binding.tvMaxMin.text = value
        })

        weeklyHistoryViewModel.records.observe(viewLifecycleOwner, {
            list = it
            rvHistory.layoutManager = LinearLayoutManager(this.requireContext())
            val listHistoryAdapter = ListHistoryAdapter(list)
            rvHistory.adapter = listHistoryAdapter
        })

        binding.tvTitle.text = sensor.name

        return binding.root
    }
}