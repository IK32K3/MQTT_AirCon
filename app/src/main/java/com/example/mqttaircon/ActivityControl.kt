package com.example.mqttaircon

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.eclipse.paho.client.mqttv3.MqttMessage

class ActivityControl : AppCompatActivity(){
    private lateinit var tvCurrentTemp: TextView
    private lateinit var tvStatus: TextView
    private lateinit var btnOn: Button
    private lateinit var btnOff: Button
    private lateinit var btnTempUp: Button
    private lateinit var btnTempDown: Button


    private var mqttClient = MqttClient.mqttClient

    private val topicCommand = "ac"

    private var currentTemperature = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        // Ánh xạ view
        tvCurrentTemp = findViewById(R.id.tv_current_temp)
        tvStatus = findViewById(R.id.tv_status)
        btnOn = findViewById(R.id.btn_power_on)
        btnOff = findViewById(R.id.btn_power_off)
        btnTempUp = findViewById(R.id.btn_temp_up)
        btnTempDown = findViewById(R.id.btn_temp_down)

        // Cập nhật nhiệt độ ban đầu
        updateTemperatureDisplay()

        // Xử lý sự kiện cho các nút
        btnOn.setOnClickListener {
            sendMqttCommand("on")
            tvStatus.text = "Trạng thái: Bật"
        }

        btnOff.setOnClickListener{
            sendMqttCommand("off")
            tvStatus.text = "Trạng thái: Tắt"
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

}