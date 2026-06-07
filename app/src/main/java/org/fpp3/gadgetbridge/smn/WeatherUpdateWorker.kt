package org.fpp3.gadgetbridge.smn

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WeatherUpdateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return runCatching {
            val settings = SettingsRepository(applicationContext).load()
            val locationProvider = LocationProvider(applicationContext)
            val coordinates = if (settings.mode == LocationMode.CURRENT) {
                locationProvider.getCurrentLocation() ?: (settings.fixedLatitude to settings.fixedLongitude)
            } else {
                settings.fixedLatitude to settings.fixedLongitude
            }
            val stations = SmnApiClient().fetchWeatherStations()
            val nearest = WeatherSelector.nearestStation(stations, coordinates.first, coordinates.second)
                ?: return Result.retry()
            WeatherBroadcastSender.send(applicationContext, nearest)
            Result.success()
        }.getOrElse {
            Result.retry()
        }
    }
}
