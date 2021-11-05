package com.kedaireka.monitoringkjabb.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.kedaireka.monitoringkjabb.R
import com.kedaireka.monitoringkjabb.model.Sensor

class ListSensorAdapter(private val listSensor: ArrayList<Sensor>) :
    RecyclerView.Adapter<ListSensorAdapter.ListViewHolder>() {
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        var tvValue: TextView = itemView.findViewById(R.id.tv_item_value)
        var imgIcon: ImageView = itemView.findViewById(R.id.iv_item_icon)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.dashboard_card, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, value, unit, created_at, urlIcon) = listSensor[position]
        val displayValue = "$value $unit"

        holder.tvName.text = name
        holder.tvValue.text = displayValue
        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context, name, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return listSensor.size
    }
}