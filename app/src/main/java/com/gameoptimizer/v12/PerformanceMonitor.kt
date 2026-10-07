package com.gameoptimizer.v12

import android.app.ActivityManager
import android.content.Context
import android.os.BatteryManager
import android.util.Log
import java.io.File

private const val SAFE_TEMP_C = 42f

data class DeviceStats(
    val totalRamMb: Long,
    val availableRamMb: Long,
    val cpuTempC: Float?,
    val batteryPercent: Int,
    val isThermalSafe: Boolean
)

class PerformanceMonitor(private val context: Context) {

    fun readStats(): DeviceStats {
        val totalRamMb = getTotalRamMb()
        val availableRamMb = getAvailableRamMb()
        val cpuTemp = readCpuTemperature()
        val batteryPercent = getBatteryPercent()
        val isThermalSafe = (cpuTemp ?: 39f) < 45f

        return DeviceStats(
            totalRamMb = totalRamMb,
            availableRamMb = availableRamMb,
            cpuTempC = cpuTemp,
            batteryPercent = batteryPercent,
            isThermalSafe = isThermalSafe
        )
    }

    fun getSuggestions(stats: DeviceStats): List<String> {
        val suggestions = mutableListOf<String>()

        if (stats.availableRamMb < 512L) {
            suggestions += "Feche apps em segundo plano para liberar memória."
        }

        if (stats.cpuTempC != null && stats.cpuTempC > SAFE_TEMP_C) {
            suggestions += "A temperatura está acima do ideal; reduza brilho e evite uso pesado."
        }

        if (stats.batteryPercent < 25) {
            suggestions += "A bateria está baixa. Ative economia de energia e reduza o brilho."
        }

        if (stats.isThermalSafe && stats.availableRamMb >= 512L) {
            suggestions += "O dispositivo está estável para jogos."
        }

        if (suggestions.isEmpty()) {
            suggestions += "Tudo parece estável; use o celular de forma equilibrada."
        }

        return suggestions
    }

    private fun getTotalRamMb(): Long {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager ?: return 0L
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        return memInfo.totalMem / (1024 * 1024)
    }

    private fun getAvailableRamMb(): Long {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager ?: return 0L
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        return memInfo.availMem / (1024 * 1024)
    }

    private fun getBatteryPercent(): Int {
        return try {
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
            batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 0
        } catch (e: Exception) {
            Log.w("PerformanceMonitor", "Não foi possível obter a bateria", e)
            0
        }
    }

    private fun readCpuTemperature(): Float? {
        val possibleFiles = listOf(
            "/sys/class/thermal/thermal_zone0/temp",
            "/sys/class/thermal/thermal_zone1/temp",
            "/sys/class/thermal/thermal_zone2/temp",
            "/sys/devices/platform/soc/soc:thermal/thermal_zone0/temp"
        )

        for (path in possibleFiles) {
            val file = File(path)
            if (!file.exists()) continue

            return try {
                val raw = file.readText().trim()
                if (raw.isNotEmpty()) raw.toFloat() / 1000f else null
            } catch (e: Exception) {
                Log.w("PerformanceMonitor", "Falha ao ler temperatura em $path", e)
                null
            }
        }

        return null
    }
}
