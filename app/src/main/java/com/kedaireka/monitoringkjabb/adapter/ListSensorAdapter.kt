package com.kedaireka.monitoringkjabb.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kedaireka.monitoringkjabb.R
import com.kedaireka.monitoringkjabb.model.Sensor
import com.kedaireka.monitoringkjabb.ui.detail.DetailSensorActivity

class ListSensorAdapter(
    private val listSensor: ArrayList<Sensor>,
    private val listThreshold: ArrayList<Map<String, Double>>
) :
    RecyclerView.Adapter<ListSensorAdapter.ListViewHolder>() {
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var card: LinearLayout = itemView.findViewById(R.id.card)
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
        val (_, name, value, unit, _, urlIcon) = listSensor[position]
        val threshold: Map<String, Double> = listThreshold[position]

        val upper = threshold["upper"]
        val lower = threshold["lower"]
        val displayValue = "$value $unit"

        holder.tvName.text = name
        holder.tvValue.text = displayValue

        if (urlIcon != "") {
            Glide
                .with(holder.itemView)
                .load(urlIcon)
                .into(holder.imgIcon)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailSensorActivity::class.java)
            intent.putExtra("data", listSensor[position])
            holder.itemView.context.startActivity(intent)
        }

        if (!(value.toDouble() <= upper!! && value.toDouble() >= lower!!)) {
            holder.card.setBackgroundColor(holder.itemView.resources.getColor(R.color.yellow))
        }
    }

    override fun getItemCount(): Int {
        return listSensor.size
    }
}