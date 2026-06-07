package org.fpp3.gadgetbridge.smn

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action) {
            val settings = SettingsRepository(context).load()
            WorkScheduler.schedulePeriodic(context, settings.updateRateMinutes)
        }
    }
}
