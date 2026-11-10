package com.akshayAshokCode.backonline.ui.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.net.toUri

/** Opens system settings pages. Some OEM builds strip a few of them, so every call is guarded. */
object SystemSettings {

    /** The Wi-Fi quick panel where available; TVs and some phones only have the full settings page. */
    fun openWifi(context: Context) {
        val openedPanel = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            start(context, Intent(Settings.Panel.ACTION_WIFI))
        if (!openedPanel) start(context, Intent(Settings.ACTION_WIFI_SETTINGS))
    }

    /** The full Wi-Fi list, where a captive network can be tapped to open its sign-in page. */
    fun openWifiList(context: Context) { start(context, Intent(Settings.ACTION_WIFI_SETTINGS)) }

    fun openAirplaneMode(context: Context) { start(context, Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)) }

    fun openNotifications(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        } else {
            appInfoIntent(context)
        }
        start(context, intent)
    }

    /** App info is where phone makers put per-app battery and background settings. */
    fun openAppInfo(context: Context) { start(context, appInfoIntent(context)) }

    private fun appInfoIntent(context: Context) =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, "package:${context.packageName}".toUri())

    /** Returns false when the page doesn't exist on this device. */
    private fun start(context: Context, intent: Intent): Boolean = try {
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        true
    } catch (_: ActivityNotFoundException) {
        false
    }
}
