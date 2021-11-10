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

class ListSensorAdapter(private val listSensor: ArrayList<Sensor>) :
    RecyclerView.Adapter<ListSensorAdapter.ListViewHolder>() {
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        var tvValue: TextView = itemView.findViewById(R.id.tv_item_value)
        var imgIcon: ImageView = itemView.findViewById(R.id.iv_item_icon)
        var llCard: LinearLayout = itemView.findViewById(R.id.card)
        var llCard2: LinearLayout = itemView.findViewById(R.id.card_2)
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
        val (id, name, value, unit, status, created_at, urlIcon) = listSensor[position]
        val displayValue = "$value $unit"

        holder.tvName.text = name
        holder.tvValue.text = displayValue

        if (urlIcon != "") {
            Glide
                .with(holder.itemView)
                .load(urlIcon)
                .into(holder.imgIcon)
        }

        selectColorByStatus(holder, status)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailSensorActivity::class.java)
            intent.putExtra("data", listSensor[position])
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listSensor.size
    }

    private fun selectColorByStatus(holder: ListViewHolder, status: Int) {
        when (status) {
            0 -> {
                holder.llCard.setBackgroundColor(holder.itemView.context.resources.getColor(R.color.blue_primary))
                holder.llCard2.setBackgroundColor(holder.itemView.context.resources.getColor(R.color.blue_primary))
            }
            1 -> {
                holder.llCard.setBackgroundColor(holder.itemView.context.resources.getColor(R.color.yellow))
                holder.llCard2.setBackgroundColor(holder.itemView.context.resources.getColor(R.color.yellow))
            }
            else -> {
                holder.llCard.setBackgroundColor(holder.itemView.context.resources.getColor(R.color.red))
                holder.llCard2.setBackgroundColor(holder.itemView.context.resources.getColor(R.color.red))
            }
        }
    }
}