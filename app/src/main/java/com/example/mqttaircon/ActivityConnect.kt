package com.example.mqttaircon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttException

class ActivityConnect : AppCompatActivity() {
    private lateinit var mqttClient: MqttAndroidClient
    private val brokerUrl = "tcp://broker.emqx.io:1883" // Địa chỉ cố định

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)

        val tvStatus: TextView = findViewById(R.id.tv_status)
        val btnConnect: Button = findViewById(R.id.btn_connect)

        btnConnect.setOnClickListener {
            connectToBroker(tvStatus)
        }
    }

    private fun connectToBroker(tvStatus: TextView) {
        val clientId = "AndroidClient_${System.currentTimeMillis()}"
        mqttClient = MqttAndroidClient(applicationContext, brokerUrl, clientId)

        try {
            mqttClient.connect(null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    tvStatus.text = "Kết nối thành công!"
                    // Chuyển tới giao diện chọn điều hòa
                    startActivity(Intent(this@ActivityConnect, ActivitySelect::class.java))
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    tvStatus.text = "Kết nối thất bại!"
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
}
