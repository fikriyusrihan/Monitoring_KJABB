package com.kedaireka.monitoringkjabb.adapter

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kedaireka.monitoringkjabb.R
import com.kedaireka.monitoringkjabb.model.Sensor
import com.kedaireka.monitoringkjabb.utils.RaindropsMapper.Companion.RAINDROPS_DICT
import com.kedaireka.monitoringkjabb.utils.RaindropsMapper.Companion.RAINDROPS_ID

class ListHistoryAdapter(private val listHistory: ArrayList<Sensor>) :
    RecyclerView.Adapter<ListHistoryAdapter.ListViewHolder>() {

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvValue: TextView = itemView.findViewById(R.id.card_history_value)
        var tvTime: TextView = itemView.findViewById(R.id.card_history_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.history_card, parent, false)

        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val sensor = listHistory[position]


        if (sensor.id == RAINDROPS_ID) {
            holder.tvValue.text = holder.itemView.context.getString(RAINDROPS_DICT[sensor.value.toInt()]!!)
        } else {
            val value = "${sensor.value} ${sensor.unit}"
            holder.tvValue.text = value
        }

        val df = DateFormat.format("yyyy-MM-dd hh:mm a", sensor.created_at.toDate())

        holder.tvTime.text = df
    }

    override fun getItemCount(): Int {
        return listHistory.size
    }
}