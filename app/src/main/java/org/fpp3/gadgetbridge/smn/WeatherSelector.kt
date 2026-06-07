package org.fpp3.gadgetbridge.smn

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object WeatherSelector {
    fun nearestStation(stations: List<SmnStationWeather>, latitude: Double, longitude: Double): SmnStationWeather? {
        return stations.minByOrNull { haversineMeters(latitude, longitude, it.latitude, it.longitude) }
    }

    private fun haversineMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusMeters = 6_371_000.0
        val latDelta = Math.toRadians(lat2 - lat1)
        val lonDelta = Math.toRadians(lon2 - lon1)
        val a = sin(latDelta / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(lonDelta / 2).pow(2)
        val c = 2 * asin(sqrt(a))
        return earthRadiusMeters * c
    }
}
