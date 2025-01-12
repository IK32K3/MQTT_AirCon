package com.example.mqttaircon

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttMessage

class ActivityControl : AppCompatActivity(){
    private lateinit var tvCurrentTemp: TextView
    private lateinit var btnPower: Button
    private lateinit var btnTempUp: Button
    private lateinit var btnTempDown: Button

    private lateinit var mqttClient: MqttAndroidClient

    private val topicCommand = "ac"
    private var isPowerOn = false

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
        mqttClient = intent.getParcelableExtra<MqttAndroidClient>("MQTT_CLIENT")
            ?: throw IllegalStateException("MQTT Client is missing")

        // Xử lý sự kiện cho các nút
        btnPower.setOnClickListener {
            togglePower()
        }

        btnTempUp.setOnClickListener {
            if (currentTemperature < 30) {
                currentTemperature++
                sendMqttCommand(currentTemperature.toString())
                updateTemperatureDisplay()
            }
        }

        btnTempDown.setOnClickListener {
            if (currentTemperature > 17) {
                currentTemperature--
                sendMqttCommand(currentTemperature.toString())
                updateTemperatureDisplay()
            }
        }
    }

    private fun sendMqttCommand(command: String) {
        val message = MqttMessage(command.toByteArray())
        mqttClient.publish(topicCommand, message)
    }

    private fun updateTemperatureDisplay() {
        tvCurrentTemp.text = "Nhiệt độ hiện tại: $currentTemperature°C"
    }

    private fun togglePower() {
        isPowerOn = !isPowerOn
        val command = if (isPowerOn) "on" else "off"
        sendMqttCommand(command)
        updatePowerDisplay()
    }

    private fun updatePowerDisplay() {
        val powerState = if (isPowerOn) "Bật" else "Tắt"
        btnPower.text = "Power: $powerState"
    }
}