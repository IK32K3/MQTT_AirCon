package com.example.mqttaircon

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttMessage

class ActivityControl : AppCompatActivity(){
    private lateinit var tvCurrentTemp: TextView
    private lateinit var btnPower: Button
    private lateinit var btnTempUp: Button
    private lateinit var btnTempDown: Button

    private lateinit var mqttClient: MqttAndroidClient

    // MQTT Broker và topic
    private val brokerUrl = "tcp://broker.emqx.io:1883"
    private val topicCommand = "aircon/command"
    private val topicStatus = "aircon/status"

    private var currentTemperature = 20 // Giá trị nhiệt độ hiện tại (giả định ban đầu)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        // Ánh xạ view
        tvCurrentTemp = findViewById(R.id.tv_current_temp)
        btnPower = findViewById(R.id.btn_power)
        btnTempUp = findViewById(R.id.btn_temp_up)
        btnTempDown = findViewById(R.id.btn_temp_down)

        // Cập nhật nhiệt độ ban đầu
        updateTemperatureDisplay()

        // Khởi tạo MQTT Client
        mqttClient = MqttAndroidClient(this, brokerUrl, "AndroidClient")

        // Kết nối MQTT
        connectToMqttBroker()

        // Xử lý sự kiện cho các nút
        btnPower.setOnClickListener {
            sendMqttCommand("TOGGLE_POWER")
        }

        btnTempUp.setOnClickListener {
            if (currentTemperature < 30) {
                currentTemperature++
                sendMqttCommand("TEMP_UP")
                updateTemperatureDisplay()
            }
        }

        btnTempDown.setOnClickListener {
            if (currentTemperature > 17) {
                currentTemperature--
                sendMqttCommand("TEMP_DOWN")
                updateTemperatureDisplay()
            }
        }
    }

    private fun connectToMqttBroker() {
        mqttClient.connect(null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                // Kết nối thành công
                mqttClient.subscribe(topicStatus, 1) { _, message ->
                    handleStatusUpdate(String(message.payload))
                }
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                // Kết nối thất bại
                tvCurrentTemp.text = "Kết nối MQTT thất bại!"
            }
        })
    }

    private fun sendMqttCommand(command: String) {
        val message = MqttMessage(command.toByteArray())
        mqttClient.publish(topicCommand, message)
    }

    private fun handleStatusUpdate(status: String) {
        // Cập nhật trạng thái từ ESP32 nếu cần
        if (status.startsWith("TEMP:")) {
            currentTemperature = status.removePrefix("TEMP:").toInt()
            updateTemperatureDisplay()
        }
    }

    private fun updateTemperatureDisplay() {
        tvCurrentTemp.text = "Nhiệt độ hiện tại: $currentTemperature°C"
    }

    override fun onDestroy() {
        super.onDestroy()
        mqttClient.disconnect()
    }
}