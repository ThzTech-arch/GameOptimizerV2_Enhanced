package com.gameoptimizer.v12

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gameoptimizer.v12.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val performanceMonitor by lazy { PerformanceMonitor(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.optimizeButton.setOnClickListener {
            refreshDashboard()
        }

        binding.modeButton.setOnClickListener {
            val stats = performanceMonitor.readStats()
            binding.summaryText.text = if (stats.healthLevel == DeviceHealthLevel.STABLE) {
                "Modo de jogo ativado com desempenho equilibrado e menor risco de aquecimento."
            } else {
                "Modo de jogo ativado em proteção cautelosa para preservar temperatura e bateria."
            }
        }

        refreshDashboard()
    }

    private fun refreshDashboard() {
        val stats = performanceMonitor.readStats()
        val suggestions = performanceMonitor.getSuggestions(stats)

        binding.ramValue.text = "${stats.availableRamMb} MB"
        binding.temperatureValue.text = stats.cpuTempC?.let { "${it}°C" } ?: "N/D"
        binding.batteryValue.text = "${stats.batteryPercent}%"
        binding.statusText.text = stats.healthLabel
        binding.summaryText.text = stats.healthMessage
        binding.tipsText.text = suggestions.joinToString("\n• ", prefix = "• ")

        val textColor = when (stats.healthLevel) {
            DeviceHealthLevel.STABLE -> R.color.accent_green
            DeviceHealthLevel.CAUTION -> R.color.accent_orange
            DeviceHealthLevel.CRITICAL -> R.color.accent_red
        }
        binding.statusText.setTextColor(ContextCompat.getColor(this, textColor))
    }
}
