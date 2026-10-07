package com.gameoptimizer.v12

import android.app.ActivityManager
import android.content.Context
import android.os.BatteryManager
import android.util.Log
import java.io.File

enum class DeviceHealthLevel {
    STABLE,
    CAUTION,
    CRITICAL
}

data class DeviceStats(
    val totalRamMb: Long,
    val availableRamMb: Long,
    val cpuTempC: Float?,
    val batteryPercent: Int,
    val healthLevel: DeviceHealthLevel
) {
    val healthLabel: String
        get() = when (healthLevel) {
            DeviceHealthLevel.STABLE -> "Sistema estável"
            DeviceHealthLevel.CAUTION -> "Atenção moderada"
            DeviceHealthLevel.CRITICAL -> "Atenção crítica"
        }

    val healthMessage: String
        get() = when (healthLevel) {
            DeviceHealthLevel.STABLE -> "Seu celular está em estado adequado para jogos e uso contínuo."
            DeviceHealthLevel.CAUTION -> "Há sinais de aquecimento ou baixa memória, mas ainda é possível manter um uso seguro."
            DeviceHealthLevel.CRITICAL -> "Temperatura ou bateria estão em nível crítico. Reduza carga e interrompa sessões longas."
        }
}

class PerformanceMonitor(private val context: Context) {

    fun readStats(): DeviceStats {
        val totalRamMb = getTotalRamMb()
        val availableRamMb = getAvailableRamMb()
        val cpuTemp = readCpuTemperature()
        val batteryPercent = getBatteryPercent()

        val healthLevel = when {
            (cpuTemp != null && cpuTemp >= 47f) || batteryPercent <= 10 -> DeviceHealthLevel.CRITICAL
            (cpuTemp != null && cpuTemp >= 42f) || availableRamMb < 512L || batteryPercent < 25 -> DeviceHealthLevel.CAUTION
            else -> DeviceHealthLevel.STABLE
        }

        return DeviceStats(
            totalRamMb = totalRamMb,
            availableRamMb = availableRamMb,
            cpuTempC = cpuTemp,
            batteryPercent = batteryPercent,
            healthLevel = healthLevel
        )
    }

    fun getSuggestions(stats: DeviceStats): List<String> {
        val suggestions = mutableListOf<String>()

        if (stats.availableRamMb < 512L) {
            suggestions += "Feche apps em segundo plano para liberar memória."
        }

        if (stats.cpuTempC != null && stats.cpuTempC >= 42f) {
            suggestions += "Reduza brilho e encerre sessões longas para controlar o calor."
        }

        if (stats.batteryPercent < 25) {
            suggestions += "Ative economia de energia e reduza notificações e brilho."
        }

        if (stats.healthLevel == DeviceHealthLevel.STABLE) {
            suggestions += "O aparelho está pronto para jogos com uso equilibrado."
        }

        if (suggestions.isEmpty()) {
            suggestions += "Seu dispositivo está em equilíbrio; mantenha o uso controlado."
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
