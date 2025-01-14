package com.example.mqttaircon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttException

class ActivityConnect : AppCompatActivity() {
    private val brokerUrl = "tcp://broker.emqx.io:1883" // Địa chỉ cố định
    private val topic = "ac"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)

        val tvStatus: TextView = findViewById(R.id.tv_status)
        val btnConnect: Button = findViewById(R.id.btn_connect)

        // Tạo MQTT Client
        initializeMqttClient(tvStatus)

        btnConnect.setOnClickListener {
            connectToBroker(tvStatus)
        }
    }

    private fun initializeMqttClient(tvStatus: TextView) {
        val clientId = "AndroidClient_${System.currentTimeMillis()}"
        MqttClient.mqttClient = MqttAndroidClient(applicationContext, brokerUrl, clientId)
        tvStatus.text = "MQTT Client đã được khởi tạo."
        Log.d("MQTT", "MQTT Client initialized with clientId: $clientId")
    }

    private fun connectToBroker(tvStatus: TextView) {
        try {
            MqttClient.mqttClient.connect(null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    tvStatus.text = "Kết nối thành công!"
                    Log.d("MQTT", "Connected to broker: $brokerUrl")

                    // Subscribe to topic sau khi kết nối thành công
                    subscribeToTopic(tvStatus)

                    // Chuyển tới giao diện chọn điều hòa
                    startActivity(Intent(this@ActivityConnect, ActivitySelect::class.java))
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    tvStatus.text = "Kết nối thất bại!"
                    Log.e("MQTT", "Failed to connect to broker", exception)
                }
            })
        } catch (e: MqttException) {
            Log.e("MQTT", "Exception while connecting to broker", e)
            e.printStackTrace()
        }
    }

    private fun subscribeToTopic(tvStatus: TextView) {
        try {
            MqttClient.mqttClient.subscribe(topic, 1, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("MQTT", "Subscribed to topic: $topic")
                    tvStatus.text = "Đã đăng ký topic: $topic"
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.e("MQTT", "Failed to subscribe to topic: $topic", exception)
                    tvStatus.text = "Đăng ký topic thất bại: $topic"
                }
            })
        } catch (e: MqttException) {
            Log.e("MQTT", "Exception while subscribing to topic: $topic", e)
            e.printStackTrace()
        }
    }
}
