package com.kedaireka.monitoringkjabb.ui.history.parameter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kedaireka.monitoringkjabb.databinding.FragmentDailyBinding
import com.kedaireka.monitoringkjabb.model.Sensor

class DailyHistoryFragment : Fragment() {

    private lateinit var dailyHistoryViewModel: DailyHistoryViewModel

    private var _binding: FragmentDailyBinding? = null
    private val binding get() = _binding!!

    private var min = 0.0
    private var max = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dailyHistoryViewModel = ViewModelProvider(this)[DailyHistoryViewModel::class.java]

        _binding = FragmentDailyBinding.inflate(inflater, container, false)

        val bundle = arguments
        val sensor = bundle!!.getParcelable<Sensor>("data") as Sensor

        dailyHistoryViewModel.getSensorHistory(sensor)

        dailyHistoryViewModel.avg.observe(viewLifecycleOwner, {
            val value = "%.2f ${sensor.unit}".format(it)
            binding.tvValue.text = value
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

        binding.tvTitle.text = sensor.name

        return binding.root
    }


}