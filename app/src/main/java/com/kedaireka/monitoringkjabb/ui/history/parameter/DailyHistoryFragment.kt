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
import com.kedaireka.monitoringkjabb.databinding.FragmentDailyBinding
import com.kedaireka.monitoringkjabb.model.Sensor

class DailyHistoryFragment : Fragment() {

    private lateinit var dailyHistoryViewModel: DailyHistoryViewModel

    private var _binding: FragmentDailyBinding? = null
    private val binding get() = _binding!!

    private lateinit var list: ArrayList<Sensor>
    private lateinit var rvHistory: RecyclerView

    private var min = 0.0
    private var max = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dailyHistoryViewModel = ViewModelProvider(this)[DailyHistoryViewModel::class.java]

        _binding = FragmentDailyBinding.inflate(inflater, container, false)

        rvHistory = binding.rvRecords
        rvHistory.setHasFixedSize(true)

        val bundle = arguments
        val sensor = bundle!!.getParcelable<Sensor>("data") as Sensor

        dailyHistoryViewModel.getSensorHistory(sensor)

        dailyHistoryViewModel.avg.observe(viewLifecycleOwner, {

            if (sensor.id == "raindrops") {
                val value = "${it.toInt()} ${sensor.unit}"
                binding.tvValue.text = value
            } else {
                val value = "%.2f ${sensor.unit}".format(it)
                binding.tvValue.text = value
            }


        })

        dailyHistoryViewModel.max.observe(viewLifecycleOwner, {
            max = it
            val value = "Max: $max | Min: $min"
            binding.tvMaxMin.text = value
        })

        dailyHistoryViewModel.min.observe(viewLifecycleOwner, {
            min = it
            val value = "Max: $max | Min: $min"
            binding.tvMaxMin.text = value
        })

        dailyHistoryViewModel.records.observe(viewLifecycleOwner, {
            list = it
            rvHistory.layoutManager = LinearLayoutManager(this.requireContext())
            val listHistoryAdapter = ListHistoryAdapter(list)
            rvHistory.adapter = listHistoryAdapter
        })

        binding.tvTitle.text = sensor.name

        return binding.root
    }


}