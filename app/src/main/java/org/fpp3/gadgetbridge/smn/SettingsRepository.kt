package org.fpp3.gadgetbridge.smn

import android.content.Context

enum class LocationMode {
    CURRENT,
    FIXED
}

data class AppSettings(
    val mode: LocationMode,
    val fixedLatitude: Double,
    val fixedLongitude: Double,
    val updateRateMinutes: Long
)

class SettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): AppSettings {
        val mode = runCatching { LocationMode.valueOf(prefs.getString(KEY_MODE, LocationMode.CURRENT.name)!!) }
            .getOrDefault(LocationMode.CURRENT)
        val lat = prefs.getString(KEY_LAT, "-34.6037")?.toDoubleOrNull() ?: -34.6037
        val lon = prefs.getString(KEY_LON, "-58.3816")?.toDoubleOrNull() ?: -58.3816
        val rate = prefs.getLong(KEY_RATE, 30L).coerceAtLeast(15L)
        return AppSettings(mode, lat, lon, rate)
    }

    fun save(settings: AppSettings) {
        prefs.edit()
            .putString(KEY_MODE, settings.mode.name)
            .putString(KEY_LAT, settings.fixedLatitude.toString())
            .putString(KEY_LON, settings.fixedLongitude.toString())
            .putLong(KEY_RATE, settings.updateRateMinutes.coerceAtLeast(15L))
            .apply()
    }

    private companion object {
        const val PREFS_NAME = "smn_settings"
        const val KEY_MODE = "location_mode"
        const val KEY_LAT = "fixed_lat"
        const val KEY_LON = "fixed_lon"
        const val KEY_RATE = "update_rate"
    }
}
