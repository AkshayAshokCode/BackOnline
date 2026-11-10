package com.akshayAshokCode.backonline.alert

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.content.ContextCompat
import com.akshayAshokCode.backonline.isTelevision
import com.akshayAshokCode.backonline.monitor.ConnectionMonitor
import com.akshayAshokCode.backonline.monitor.ConnectionState
import com.akshayAshokCode.backonline.monitor.InternetStatus

/**
 * TV only, while "Automatic alerts" is on: watches the connection all the time.
 * TVs are always plugged in, so this costs no battery and avoids the phone problem of battery
 * savers killing the alert.
 *
 * When the internet has been down for [MIN_OUTAGE_MS], it arms [AlertService] automatically
 * (counted from the start of the outage), so the TV plays a sound when the internet is back.
 * Shorter blips are ignored. Outages longer than that are recorded in [OutageLog].
 */
class AutoWatchService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var monitor: ConnectionMonitor
    private var isWatching = false

    /** When the current outage started, or 0 when online. */
    private var outageStart = 0L

    private val armAfterDelay = Runnable {
        if (outageStart != 0L && AlertService.armedSince.value == null && !AlertService.isRinging.value) {
            AlertService.arm(this, since = outageStart)
        }
    }

    override fun onCreate() {
        super.onCreate()
        AlertNotifications.createChannels(this)
        monitor = ConnectionMonitor(this) { onStateChanged(it) }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isTelevision() || !AlertPrefs.isAutoWatch(this) || !startInForeground()) {
            stopSelf()
            return START_NOT_STICKY
        }
        if (!isWatching) {
            isWatching = true
            monitor.start()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        if (isWatching) monitor.stop()
        isWatching = false
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startInForeground(): Boolean = try {
        val notification = AlertNotifications.watching(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(AlertNotifications.ID_WATCHING, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(AlertNotifications.ID_WATCHING, notification)
        }
        true
    } catch (_: IllegalStateException) { // includes ForegroundServiceStartNotAllowedException
        false
    } catch (_: SecurityException) {
        false
    }

    private fun onStateChanged(state: ConnectionState) {
        val now = System.currentTimeMillis()
        when {
            // Wi-Fi switched off on purpose (no other way to connect): not an outage
            state.blocker != null -> {
                handler.removeCallbacks(armAfterDelay)
                outageStart = 0L
            }
            state.status != InternetStatus.ONLINE -> if (outageStart == 0L) {
                outageStart = now
                handler.postDelayed(armAfterDelay, MIN_OUTAGE_MS)
            }
            outageStart != 0L -> {
                handler.removeCallbacks(armAfterDelay)
                if (now - outageStart >= MIN_OUTAGE_MS) OutageLog.record(this, outageStart, now)
                outageStart = 0L
            }
        }
    }

    companion object {
        /** Outages shorter than this (router hiccups) are ignored. */
        const val MIN_OUTAGE_MS = 2 * 60 * 1000L

        /** Starts watching if this is a TV and automatic alerts are on; otherwise does nothing. */
        fun startIfEnabled(context: Context) {
            if (!context.isTelevision() || !AlertPrefs.isAutoWatch(context)) return
            try {
                ContextCompat.startForegroundService(context, Intent(context, AutoWatchService::class.java))
            } catch (_: IllegalStateException) {
                // Not allowed from here (background start limits) — the TV board starts it when opened
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, AutoWatchService::class.java))
        }
    }
}
