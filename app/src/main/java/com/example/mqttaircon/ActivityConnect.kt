package com.example.mqttaircon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException

class ActivityConnect : AppCompatActivity() {
    private val brokerUrl = "tcp://broker.emqx.io:1883"
    private val clientId = "AndroidClient"
    private val topic = "ac"
    private val username = "emqx"
    private val password = "public"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)

        val btnConnect: Button = findViewById(R.id.btn_connect)

        initializeMqttClient()

        btnConnect.setOnClickListener {
            connectToBroker()
        }
    }

    private fun initializeMqttClient() {
        MqttClient.mqttClient = MqttAndroidClient(applicationContext, brokerUrl, clientId)
        Log.d("MQTT", "MQTT Client initialized with clientId: $clientId")
    }

    private fun connectToBroker() {
        val options = MqttConnectOptions().apply {
            userName = username
            password = this@ActivityConnect.password.toCharArray()
        }

        try {
            MqttClient.mqttClient.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Toast.makeText(this@ActivityConnect, "Kết nối thành công!", Toast.LENGTH_SHORT).show()
                    Log.d("MQTT", "Connected to broker: $brokerUrl")

                    subscribeToTopic()

                    startActivity(Intent(this@ActivityConnect, ActivitySelect::class.java))
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Toast.makeText(this@ActivityConnect, "Kết nối thất bại!", Toast.LENGTH_SHORT).show()
                    Log.e("MQTT", "Failed to connect to broker", exception)
                }
            })
        } catch (e: MqttException) {
            Log.e("MQTT", "Exception while connecting to broker", e)
            Toast.makeText(this, "Lỗi khi kết nối: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun subscribeToTopic() {
        try {
            MqttClient.mqttClient.subscribe(topic, 1, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("MQTT", "Subscribed to topic: $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.e("MQTT", "Failed to subscribe to topic: $topic", exception)
                }
            })
        } catch (e: MqttException) {
            Log.e("MQTT", "Exception while subscribing to topic: $topic", e)
            e.printStackTrace()
        }
    }
}