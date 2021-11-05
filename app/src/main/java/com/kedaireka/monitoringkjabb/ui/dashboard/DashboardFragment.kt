package com.kedaireka.monitoringkjabb.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kedaireka.monitoringkjabb.R
import com.kedaireka.monitoringkjabb.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.fragmentTitle
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

//        val textDO = binding.indicatorTitle1
//        val textTemp = binding.indicatorTitle2
//        val textPH = binding.indicatorTitle3
//        val textSalinity = binding.indicatorTitle4
//
//        dashboardViewModel.data.observe(viewLifecycleOwner, Observer {
//            textDO.text = it["sensor_do"]
//            textTemp.text = it["sensor_temperature"]
//            textPH.text = it["sensor_ph"]
//            textSalinity.text = it["sensor_salinity"]
//        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}