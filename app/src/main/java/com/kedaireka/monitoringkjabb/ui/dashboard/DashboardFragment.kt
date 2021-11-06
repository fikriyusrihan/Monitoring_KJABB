package com.kedaireka.monitoringkjabb.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kedaireka.monitoringkjabb.adapter.ListSensorAdapter
import com.kedaireka.monitoringkjabb.databinding.FragmentDashboardBinding
import com.kedaireka.monitoringkjabb.model.Sensor

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var rvSensor: RecyclerView

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private var list = ArrayList<Sensor>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        rvSensor = binding.rvHeroes
        rvSensor.setHasFixedSize(true)

        dashboardViewModel.isLoading.observe(viewLifecycleOwner, {
            showLoading(it)
        })

        dashboardViewModel.data.observe(viewLifecycleOwner, {
            list = it
            showRecyclerView()
        })


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showRecyclerView() {
        rvSensor.layoutManager = LinearLayoutManager(this.context)
        val listSensorAdapter = ListSensorAdapter(list)
        rvSensor.adapter = listSensorAdapter
    }

    private fun showLoading(bool: Boolean) {
    }
}