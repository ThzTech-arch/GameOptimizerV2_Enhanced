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
            updateStatusPanel()
        }

        updateStatusPanel()
    }

    private fun updateStatusPanel() {
        val stats = performanceMonitor.readStats()
        val suggestions = performanceMonitor.getSuggestions(stats)

        binding.ramValue.text = "${stats.availableRamMb} MB"
        binding.temperatureValue.text = stats.cpuTempC?.let { "${it}°C" } ?: "N/D"
        binding.batteryValue.text = "${stats.batteryPercent}%"
        binding.statusText.text = if (stats.isThermalSafe) {
            "Sistema estável"
        } else {
            "Atenção térmica"
        }
        binding.summaryText.text = if (stats.isThermalSafe) {
            "Seu celular está em estado adequado para jogos e uso contínuo."
        } else {
            "A temperatura está elevada. Reduza brilho, evite apps pesados e pause sessões prolongadas."
        }
        binding.tipsText.text = suggestions.joinToString("\n• ", prefix = "• ")
    }
}
