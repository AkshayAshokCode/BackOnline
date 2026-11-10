package com.akshayAshokCode.backonline.alert

import android.annotation.SuppressLint
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.core.app.NotificationManagerCompat
import com.akshayAshokCode.backonline.R
import com.akshayAshokCode.backonline.homeScreenIntent
import com.akshayAshokCode.backonline.monitor.ConnectionMonitor
import com.akshayAshokCode.backonline.monitor.InternetStatus

/**
 * Quick Settings tile: arm or cancel the alert without opening the app.
 * When arming is not possible right now (already online, Wi-Fi off, airplane mode, notifications
 * blocked) it opens the app, which explains what to do.
 */
class AlertTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        refreshTile()
    }

    override fun onClick() {
        super.onClick()
        if (AlertService.armedSince.value != null) {
            AlertService.cancel(this)
            return
        }

        val state = ConnectionMonitor.snapshot(this)
        val canArm = state.blocker == null &&
            state.status != InternetStatus.ONLINE &&
            NotificationManagerCompat.from(this).areNotificationsEnabled()
        if (!canArm) {
            openApp()
            return
        }

        try {
            AlertService.arm(this)
        } catch (_: IllegalStateException) {
            // Android 12+ may refuse to start a foreground service from here — fall back to the app
            openApp()
        }
    }

    private fun refreshTile() {
        val tile = qsTile ?: return
        val isArmed = AlertService.armedSince.value != null
        tile.state = if (isArmed) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = getString(R.string.tile_label)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = getString(if (isArmed) R.string.tile_waiting else R.string.tile_off)
        }
        tile.updateTile()
    }

    @SuppressLint("StartActivityAndCollapseDeprecated") // the Intent overload is the only one below API 34
    private fun openApp() {
        val open = Runnable {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startActivityAndCollapse(AlertNotifications.openApp(this))
            } else {
                @Suppress("DEPRECATION")
                startActivityAndCollapse(homeScreenIntent())
            }
        }
        if (isLocked) unlockAndRun(open) else open.run()
    }
}
