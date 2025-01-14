package com.example.mqttaircon

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.eclipse.paho.client.mqttv3.MqttMessage

class ActivitySelect : AppCompatActivity(){
    private var mqttClient = MqttClient.mqttClient
    private val topic = "ac"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        val airConditionerList = listOf("LG", "Panasonic", "Samsung", "Daikin")

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_air_conditioner)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = AirConditionerAdapter(airConditionerList) { selectedAC ->
            // Gửi tên điều hòa lên broker
            publishSelectedAC(selectedAC)

            // Chuyển tới giao diện điều khiển
            val intent = Intent(this, ActivityControl::class.java)
            startActivity(intent)
        }
    }

    private fun publishSelectedAC(selectedAC: String) {
        try {
            val message = MqttMessage(selectedAC.toByteArray())
            mqttClient.publish(topic, message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}