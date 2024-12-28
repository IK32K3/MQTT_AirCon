package com.example.mqttaircon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AirConditionerAdapter(
    private val airConditioners: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<AirConditionerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_ac_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ac, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = airConditioners[position]
        holder.itemView.setOnClickListener {
            onItemClick(airConditioners[position])
        }
    }

    override fun getItemCount(): Int = airConditioners.size
}
