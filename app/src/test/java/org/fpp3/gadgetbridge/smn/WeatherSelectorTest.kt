package org.fpp3.gadgetbridge.smn

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WeatherSelectorTest {
    @Test
    fun nearestStation_returnsClosestStation() {
        val stations = listOf(
            station("Far", -33.0, -60.0),
            station("Near", -34.60, -58.39),
            station("Medium", -35.0, -59.0)
        )

        val result = WeatherSelector.nearestStation(stations, -34.6037, -58.3816)

        assertEquals("Near", result?.name)
    }

    @Test
    fun nearestStation_returnsNullWhenNoStations() {
        assertNull(WeatherSelector.nearestStation(emptyList(), -34.6, -58.3))
    }

    @Test
    fun conditionMapper_mapsKnownCodes() {
        assertEquals(800, ConditionMapper.toOpenWeatherCode(0))
        assertEquals(211, ConditionMapper.toOpenWeatherCode(4))
        assertEquals(771, ConditionMapper.toOpenWeatherCode(20))
    }

    private fun station(name: String, lat: Double, lon: Double): SmnStationWeather {
        return SmnStationWeather(
            name = name,
            country = "AR",
            latitude = lat,
            longitude = lon,
            temperature = 20f,
            minTemperature = 15f,
            maxTemperature = 25f,
            humidity = 50,
            pressure = 1000f,
            windSpeedMetersPerSecond = 2f,
            windDirectionDegrees = 90,
            conditionCode = 800,
            summary = "clear",
            timestampMillis = 0L
        )
    }
}
