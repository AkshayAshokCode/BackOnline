package com.akshayAshokCode.backonline.monitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.content.ContextCompat
import java.util.concurrent.ConcurrentHashMap

/**
 * Which connection BackOnline watches — chosen automatically, never asked:
 * - HOME: Wi-Fi or Ethernet. Used when Wi-Fi is switched on, when Ethernet is present, or on devices
 *   without mobile data (e.g. TVs). Wi-Fi switched on means the user expects Wi-Fi even while it is
 *   not connected (router without power).
 * - MOBILE: mobile data, when Wi-Fi is switched off on a phone.
 */
enum class Watched { HOME, MOBILE }

/** The network type actually carrying (or last seen for) the watched connection. */
enum class Medium { WIFI, ETHERNET, MOBILE }

enum class InternetStatus {
    ONLINE,          // Android confirmed the internet works (validated)
    SIGN_IN_NEEDED,  // hotel / café / airport Wi-Fi waiting for its sign-in page (captive portal)
    NO_INTERNET,     // connected, but the internet does not work (router up, provider down)
    DISCONNECTED     // not connected
}

/** Something the user must change before the watched connection can ever come back. */
enum class Blocker {
    WIFI_OFF,      // Wi-Fi off on a device with no other way to connect
    AIRPLANE_MODE  // watching mobile data, but airplane mode is on
}

data class ConnectionState(
    val watched : Watched,
    val status  : InternetStatus,
    val medium  : Medium?,
    val blocker : Blocker? = null
)

/**
 * Watches Wi-Fi, Ethernet and mobile networks and reports the state of the watched connection.
 * [onChange] is called on the main thread, and only when the state changes.
 */
class ConnectionMonitor(
    context: Context,
    private val onChange: (ConnectionState) -> Unit
) {
    private val appContext = context.applicationContext
    private val connectivityManager = appContext.getSystemService(ConnectivityManager::class.java)
    private val mainHandler = Handler(Looper.getMainLooper())

    // Written on the connectivity thread, read on main.
    private val networks = ConcurrentHashMap<Network, TrackedNetwork>()

    private var lastState: ConnectionState? = null
    private var isRunning = false

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            // API 24–25 does not always follow up with onCapabilitiesChanged, so read them here
            track(network, connectivityManager.getNetworkCapabilities(network))
        }

        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            track(network, capabilities)
        }

        override fun onLost(network: Network) {
            networks.remove(network)
            publish()
        }
    }

    // Wi-Fi or airplane mode switched — changes which connection is watched
    private val settingsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) = publish()
    }

    /**
     * While connected without internet, Android re-checks with growing gaps (up to minutes), so
     * the alert could be minutes late after the provider comes back. Every 30 s, ask for a re-check.
     */
    private val recheck = object : Runnable {
        override fun run() {
            if (!isRunning) return
            val watched = watchedOf(appContext, networks.values)
            networks.filterValues { it.watched == watched && !it.validated && !it.captive }.keys.forEach { network ->
                try {
                    // "true" on an unvalidated network is what triggers a re-check: Android only
                    // re-evaluates when an app's report disagrees with its current result.
                    connectivityManager.reportNetworkConnectivity(network, true)
                } catch (_: SecurityException) {
                    // Some OEM builds restrict this — the system's own re-checks still apply
                }
            }
            mainHandler.postDelayed(this, RECHECK_INTERVAL_MS)
        }
    }

    fun start() {
        if (isRunning) return
        isRunning = true
        lastState = null
        networks.clear()
        networks.putAll(currentNetworks(connectivityManager))
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, callback)
        ContextCompat.registerReceiver(
            appContext,
            settingsReceiver,
            IntentFilter().apply {
                addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
                addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            },
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        publish()
    }

    fun stop() {
        if (!isRunning) return
        isRunning = false
        mainHandler.removeCallbacks(recheck)
        try {
            connectivityManager.unregisterNetworkCallback(callback)
        } catch (_: IllegalArgumentException) {
            // already unregistered
        }
        try {
            appContext.unregisterReceiver(settingsReceiver)
        } catch (_: IllegalArgumentException) {
            // already unregistered
        }
        networks.clear()
    }

    private fun track(network: Network, capabilities: NetworkCapabilities?) {
        val tracked = capabilities?.let { trackedNetworkOf(it) }
        if (tracked == null) networks.remove(network) else networks[network] = tracked
        publish()
    }

    private fun publish() {
        val state = stateOf(appContext, networks.values)
        mainHandler.post {
            if (!isRunning || state == lastState) return@post
            lastState = state
            mainHandler.removeCallbacks(recheck)
            if (state.status == InternetStatus.NO_INTERNET) {
                mainHandler.postDelayed(recheck, RECHECK_INTERVAL_MS)
            }
            onChange(state)
        }
    }

    private data class TrackedNetwork(
        val medium    : Medium,
        val validated : Boolean,
        val captive   : Boolean
    ) {
        val watched get() = if (medium == Medium.MOBILE) Watched.MOBILE else Watched.HOME
    }

    companion object {
        private const val RECHECK_INTERVAL_MS = 30_000L

        /** One-off synchronous check, for the tile, the boot receiver and the first frame. */
        fun snapshot(context: Context): ConnectionState {
            val app = context.applicationContext
            val cm = app.getSystemService(ConnectivityManager::class.java)
            return stateOf(app, currentNetworks(cm).values)
        }

        fun isWifiEnabled(context: Context): Boolean =
            context.applicationContext.getSystemService(WifiManager::class.java)?.isWifiEnabled == true

        /** True when the device can connect in any of the ways BackOnline watches. */
        fun isSupported(context: Context): Boolean {
            val pm = context.packageManager
            return pm.hasSystemFeature(PackageManager.FEATURE_WIFI) ||
                pm.hasSystemFeature(PackageManager.FEATURE_ETHERNET) ||
                hasMobileData(context)
        }

        private fun hasMobileData(context: Context): Boolean =
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)

        private fun hasEthernet(context: Context): Boolean =
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_ETHERNET)

        private fun isAirplaneModeOn(context: Context): Boolean =
            Settings.Global.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0

        private fun watchedOf(context: Context, tracked: Collection<TrackedNetwork>): Watched {
            val ethernetPresent = tracked.any { it.medium == Medium.ETHERNET }
            return if (isWifiEnabled(context) || ethernetPresent || !hasMobileData(context)) Watched.HOME
            else Watched.MOBILE
        }

        private fun stateOf(context: Context, tracked: Collection<TrackedNetwork>): ConnectionState {
            val watched = watchedOf(context, tracked)
            val candidates = tracked.filter { it.watched == watched }
            val status = when {
                candidates.any { it.validated } -> InternetStatus.ONLINE
                candidates.any { it.captive } -> InternetStatus.SIGN_IN_NEEDED
                candidates.isNotEmpty() -> InternetStatus.NO_INTERNET
                else -> InternetStatus.DISCONNECTED
            }
            // Prefer the network that works; Ethernet over Wi-Fi when both do
            val preferred = candidates.filter { it.validated }.ifEmpty { candidates }
            val medium = when {
                preferred.any { it.medium == Medium.ETHERNET } -> Medium.ETHERNET
                preferred.any { it.medium == Medium.WIFI } -> Medium.WIFI
                watched == Watched.MOBILE -> Medium.MOBILE
                else -> null
            }
            val blocker = when {
                // A wired TV whose router is off loses its Ethernet link too: that must not block arming
                watched == Watched.HOME && !isWifiEnabled(context) && !hasEthernet(context) &&
                    candidates.isEmpty() -> Blocker.WIFI_OFF
                watched == Watched.MOBILE && isAirplaneModeOn(context) -> Blocker.AIRPLANE_MODE
                else -> null
            }
            return ConnectionState(watched, status, medium, blocker)
        }

        private fun trackedNetworkOf(caps: NetworkCapabilities): TrackedNetwork? {
            if (!caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) return null
            val medium = when {
                caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> Medium.ETHERNET
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> Medium.WIFI
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> Medium.MOBILE
                else -> return null // Bluetooth tethering, VPN-only: not watched
            }
            return TrackedNetwork(
                medium    = medium,
                validated = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED),
                captive   = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)
            )
        }

        @Suppress("DEPRECATION") // allNetworks is still the only synchronous way to list networks
        private fun currentNetworks(cm: ConnectivityManager): Map<Network, TrackedNetwork> =
            cm.allNetworks.mapNotNull { network ->
                cm.getNetworkCapabilities(network)?.let { trackedNetworkOf(it) }?.let { network to it }
            }.toMap()
    }
}
