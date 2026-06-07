package org.fpp3.gadgetbridge.smn

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkScheduler {
    private const val PERIODIC_WORK_NAME = "smn_periodic_weather"

    fun schedulePeriodic(context: Context, rateMinutes: Long) {
        val request = PeriodicWorkRequestBuilder<WeatherUpdateWorker>(rateMinutes.coerceAtLeast(15L), TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun enqueueImmediate(context: Context) {
        val request = OneTimeWorkRequestBuilder<WeatherUpdateWorker>().build()
        WorkManager.getInstance(context).enqueue(request)
    }
}
