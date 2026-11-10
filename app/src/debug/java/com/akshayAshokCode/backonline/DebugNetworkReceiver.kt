package com.akshayAshokCode.backonline

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log

/**
 * Debug builds only. Forces Android to re-check every network's internet access, so an outage
 * can be simulated on emulators that don't allow root (e.g. Google TV images):
 *
 *   adb shell settings put global captive_portal_https_url http://127.0.0.1:9/   (and http/fallback)
 *   adb shell am broadcast -a com.akshayAshokCode.backonline.debug.RECHECK -p com.akshayAshokCode.backonline
 *
 * Reporting "no connectivity" on a validated network is what makes Android re-evaluate it.
 */
class DebugNetworkReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_FORCE_TV) {
            DebugFlags.setForceTelevision(context, intent.getBooleanExtra("on", false))
            Log.d("BackOnlineDebug", "Force TV mode: ${DebugFlags.forceTelevision(context)}")
            return
        }
        val cm = context.getSystemService(ConnectivityManager::class.java)
        @Suppress("DEPRECATION")
        cm.allNetworks.forEach { network ->
            cm.reportNetworkConnectivity(network, false)
            Log.d("BackOnlineDebug", "Asked Android to re-check $network")
        }
    }

    private companion object {
        const val ACTION_FORCE_TV = "com.akshayAshokCode.backonline.debug.FORCE_TV"
    }
}
