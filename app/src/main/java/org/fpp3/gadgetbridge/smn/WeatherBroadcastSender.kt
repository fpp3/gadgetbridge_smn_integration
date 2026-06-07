package org.fpp3.gadgetbridge.smn

import android.content.Context
import android.content.Intent

object WeatherBroadcastSender {
    private const val ACTION_EXTERNAL_WEATHER = "nodomain.freeyourgadget.gadgetbridge.externalweather"
    private const val GADGETBRIDGE_PACKAGE = "nodomain.freeyourgadget.gadgetbridge"

    fun send(context: Context, weather: SmnStationWeather) {
        val intent = Intent(ACTION_EXTERNAL_WEATHER).apply {
            setPackage(GADGETBRIDGE_PACKAGE)
            putExtra("appid", BuildConfig.APPLICATION_ID)
            putExtra("currentWeatherTemperature", weather.temperature)
            putExtra("currentWeatherTemperatureUnit", "C")
            putExtra("currentWeatherSummary", weather.summary)
            putExtra("currentWeatherConditionCode", weather.conditionCode)
            putExtra("currentWeatherHumidity", weather.humidity)
            putExtra("currentWeatherPressure", weather.pressure)
            putExtra("currentWeatherWindSpeed", weather.windSpeedMetersPerSecond)
            putExtra("currentWeatherWindDirection", weather.windDirectionDegrees)
            putExtra("currentWeatherRainfall", 0f)
            putExtra("locationCity", weather.name)
            putExtra("locationCountry", weather.country)
            putExtra("forecastTodayMinTemp", weather.minTemperature)
            putExtra("forecastTodayMaxTemp", weather.maxTemperature)
            putExtra("timestamp", weather.timestampMillis)
        }
        context.sendBroadcast(intent)
    }
}
