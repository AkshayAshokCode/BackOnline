package com.akshayAshokCode.backonline.alert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Resumes a wait after the device restarts (power cuts often restart devices) or after the app
 * is updated — both stop the services. On TVs it also restarts the automatic watcher.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_MY_PACKAGE_REPLACED
        ) return
        // TVs with automatic alerts start watching again after every boot
        AutoWatchService.startIfEnabled(context)
        if (AlertPrefs.getArmedAt(context) == 0L) return
        try {
            AlertService.resume(context)
        } catch (_: IllegalStateException) {
            // Android refused to start the service from here: tell the user instead of failing silently
            AlertPrefs.clearArmedAt(context)
            AlertNotifications.showStoppedBySystem(context)
        }
    }
}
