package com.example.mqttaircon

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ActivitySelect : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        val airConditionerList = listOf("Điều hòa LG", "Điều hòa Panasonic", "Điều hòa Samsung")

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_air_conditioner)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = AirConditionerAdapter(airConditionerList) { selectedAC ->
            // Chuyển tới giao diện điều khiển
            val intent = Intent(this, ActivityControl::class.java)
            intent.putExtra("selectedAC", selectedAC)
            startActivity(intent)
        }
    }
}