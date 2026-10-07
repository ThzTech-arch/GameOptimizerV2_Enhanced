package com.gameoptimizer.v12

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gameoptimizer.v12.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val performanceMonitor by lazy { PerformanceMonitor(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.optimizeButton.setOnClickListener {
            updateStatus()
        }

        updateStatus()
    }

    private fun updateStatus() {
        val stats = performanceMonitor.readStats()
        val suggestions = performanceMonitor.getSuggestions(stats)

        val statusText = buildString {
            append("Memória disponível: ")
            append(stats.availableRamMb)
            append(" MB\n")
            append("Temperatura: ")
            append(stats.cpuTempC?.let { "${it}°C" } ?: "não disponível")
            append("\nBateria: ")
            append(stats.batteryPercent)
            append("%")
        }

        binding.statusText.text = statusText
        binding.summaryText.text = if (stats.isThermalSafe) {
            "Dispositivo está em estado estável para jogos."
        } else {
            "Atenção: a temperatura está elevada. Reduza o brilho e evite uso excessivo."
        }
        binding.tipsText.text = suggestions.joinToString("\n• ", prefix = "• ")
    }
}
