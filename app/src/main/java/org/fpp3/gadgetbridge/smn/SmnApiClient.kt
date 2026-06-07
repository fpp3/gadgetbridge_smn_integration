package org.fpp3.gadgetbridge.smn

import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

data class SmnStationWeather(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val temperature: Float,
    val minTemperature: Float,
    val maxTemperature: Float,
    val humidity: Int,
    val pressure: Float,
    val windSpeedMetersPerSecond: Float,
    val windDirectionDegrees: Int,
    val conditionCode: Int,
    val summary: String,
    val timestampMillis: Long
)

class SmnApiClient {
    fun fetchWeatherStations(): List<SmnStationWeather> {
        val connection = URL(WEATHER_ENDPOINT).openConnection() as HttpURLConnection
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        connection.requestMethod = "GET"

        connection.inputStream.bufferedReader().use { reader ->
            val json = JSONArray(reader.readText())
            val result = mutableListOf<SmnStationWeather>()
            for (index in 0 until json.length()) {
                val item = json.optJSONObject(index) ?: continue
                val weather = item.optJSONObject("weather") ?: continue
                val forecast = item.optJSONObject("forecast")
                val today = forecast?.optJSONObject("forecast")?.optJSONObject("0")
                val temp = weather.optDouble("temp", Double.NaN)
                val lat = item.optDouble("lat", Double.NaN)
                val lon = item.optDouble("lon", Double.NaN)
                if (temp.isNaN() || lat.isNaN() || lon.isNaN()) {
                    continue
                }
                val station = SmnStationWeather(
                    name = item.optString("name", "Unknown"),
                    country = "AR",
                    latitude = lat,
                    longitude = lon,
                    temperature = temp.toFloat(),
                    minTemperature = today?.optDouble("temp_min", temp)?.toFloat() ?: temp.toFloat(),
                    maxTemperature = today?.optDouble("temp_max", temp)?.toFloat() ?: temp.toFloat(),
                    humidity = weather.optInt("humidity", 0),
                    pressure = weather.optDouble("pressure", 0.0).toFloat(),
                    windSpeedMetersPerSecond = (weather.optDouble("wind_speed", 0.0) / 3.6).toFloat(),
                    windDirectionDegrees = cardinalToDegrees(weather.optString("wind_deg", "")),
                    conditionCode = ConditionMapper.toOpenWeatherCode(weather.optInt("id", -1)),
                    summary = weather.optString("description", ""),
                    timestampMillis = item.optLong("updated", System.currentTimeMillis())
                )
                result.add(station)
            }
            return result
        }
    }

    private fun cardinalToDegrees(cardinal: String): Int = when (cardinal.lowercase()) {
        "n", "norte" -> 0
        "ne", "noreste" -> 45
        "e", "este" -> 90
        "se", "sudeste", "sureste" -> 135
        "s", "sur" -> 180
        "so", "sudoeste", "suroeste", "o", "oeste" -> 270
        "no", "noroeste" -> 315
        else -> 0
    }

    companion object {
        private const val WEATHER_ENDPOINT = "https://ws.smn.gob.ar/map_items/weather"
    }
}
