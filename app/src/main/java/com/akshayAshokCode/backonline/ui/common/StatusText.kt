package com.akshayAshokCode.backonline.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.akshayAshokCode.backonline.R
import com.akshayAshokCode.backonline.monitor.Blocker
import com.akshayAshokCode.backonline.monitor.ConnectionMonitor
import com.akshayAshokCode.backonline.monitor.ConnectionState
import com.akshayAshokCode.backonline.monitor.InternetStatus
import com.akshayAshokCode.backonline.monitor.Medium
import com.akshayAshokCode.backonline.monitor.SignalLevel
import com.akshayAshokCode.backonline.monitor.SignalReading
import com.akshayAshokCode.backonline.monitor.Watched
import androidx.compose.ui.platform.LocalContext

/** Colour of the status: green online, amber connected-but-waiting, muted red otherwise. */
fun statusColor(state: ConnectionState): Color = when {
    state.blocker != null -> BrandColors.Red
    state.status == InternetStatus.ONLINE -> BrandColors.Green
    state.status == InternetStatus.NO_INTERNET || state.status == InternetStatus.SIGN_IN_NEEDED -> BrandColors.Amber
    else -> BrandColors.Red
}

@Composable
fun statusTitle(state: ConnectionState, recentlyBack: Boolean): String {
    val context = LocalContext.current
    return stringResource(
        when {
            state.blocker == Blocker.WIFI_OFF -> R.string.status_wifi_off
            state.blocker == Blocker.AIRPLANE_MODE -> R.string.status_airplane
            state.status == InternetStatus.ONLINE ->
                if (recentlyBack) R.string.status_back_online else R.string.status_online
            state.status == InternetStatus.SIGN_IN_NEEDED -> R.string.status_sign_in
            state.status == InternetStatus.NO_INTERNET -> R.string.status_no_internet
            state.watched == Watched.MOBILE -> R.string.status_disconnected_mobile
            ConnectionMonitor.isWifiEnabled(context) -> R.string.status_disconnected_wifi
            else -> R.string.status_disconnected_home
        }
    )
}

/** e.g. "Wi-Fi · -54 dBm · Excellent · 5 GHz", "Ethernet", "Mobile data · -95 dBm · Good · Carrier". */
@Composable
fun connectionLine(state: ConnectionState, signal: SignalReading?): String? {
    if (state.blocker != null || state.status == InternetStatus.DISCONNECTED) return null
    val medium = when (state.medium) {
        Medium.WIFI -> stringResource(R.string.medium_wifi)
        Medium.ETHERNET -> stringResource(R.string.medium_ethernet)
        Medium.MOBILE -> stringResource(R.string.medium_mobile)
        null -> return null
    }
    val parts = mutableListOf(medium)
    if (signal != null) {
        signal.dbm?.let { parts += "$it dBm" }
        parts += stringResource(
            when (signal.level) {
                SignalLevel.EXCELLENT -> R.string.signal_excellent
                SignalLevel.GOOD -> R.string.signal_good
                SignalLevel.FAIR -> R.string.signal_fair
                SignalLevel.WEAK -> R.string.signal_weak
            }
        )
        signal.detail?.let { parts += it }
    }
    return parts.joinToString("  ·  ")
}
