package com.akshayAshokCode.backonline.ui.common

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.akshayAshokCode.backonline.alert.AlertPrefs
import com.akshayAshokCode.backonline.alert.AlertService
import com.akshayAshokCode.backonline.alert.AutoWatchService
import com.akshayAshokCode.backonline.alert.Outage
import com.akshayAshokCode.backonline.alert.OutageLog
import com.akshayAshokCode.backonline.monitor.ConnectionMonitor
import com.akshayAshokCode.backonline.monitor.ConnectionState
import com.akshayAshokCode.backonline.monitor.InternetStatus
import com.akshayAshokCode.backonline.monitor.SignalReading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** The alert was stopped by the phone in the background; found when the user opens the app. */
enum class StoppedBySystem {
    RESUMED, // the internet is still down, so the wait was turned back on
    MISSED   // the internet came back while the alert was stopped
}

/** Shared by the phone screen and the TV board. The monitor runs only while a screen is visible. */
class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val context get() = getApplication<Application>()

    /** Starts from a synchronous snapshot, so the first frame never flashes a wrong status. */
    val state: StateFlow<ConnectionState> = callbackFlow {
        val monitor = ConnectionMonitor(context) { trySend(it) }
        monitor.start()
        awaitClose { monitor.stop() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_DELAY_MS), ConnectionMonitor.snapshot(app))

    /** Signal strength, polled once a second while visible and connected. */
    @OptIn(ExperimentalCoroutinesApi::class)
    val signal: StateFlow<SignalReading?> = state
        .map { if (it.blocker == null && it.status != InternetStatus.DISCONNECTED) it.medium else null }
        .distinctUntilChanged()
        .flatMapLatest { medium ->
            if (medium == null) flowOf<SignalReading?>(null)
            else flow<SignalReading?> {
                while (true) {
                    emit(SignalReading.read(context, medium))
                    delay(SIGNAL_POLL_MS)
                }
            }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_DELAY_MS), null)

    val armedSince = AlertService.armedSince
    val isRinging  = AlertService.isRinging
    val lastBack   = AlertService.lastBack

    private val _ringMode = MutableStateFlow(AlertPrefs.isRing(app))
    val ringMode: StateFlow<Boolean> = _ringMode.asStateFlow()

    private val _autoWatch = MutableStateFlow(AlertPrefs.isAutoWatch(app))
    /** TV only: automatic alerts after every outage. */
    val autoWatch: StateFlow<Boolean> = _autoWatch.asStateFlow()

    /** Outages recorded by the TV's automatic watcher. */
    val outages: StateFlow<List<Outage>?> = OutageLog.entries(app)

    private val _stoppedBySystem = MutableStateFlow<StoppedBySystem?>(null)
    val stoppedBySystem: StateFlow<StoppedBySystem?> = _stoppedBySystem.asStateFlow()

    fun setRingMode(ring: Boolean) {
        AlertPrefs.setRing(context, ring)
        _ringMode.value = ring
    }

    fun setAutoWatch(on: Boolean) {
        AlertPrefs.setAutoWatch(context, on)
        _autoWatch.value = on
        if (on) AutoWatchService.startIfEnabled(context) else AutoWatchService.stop(context)
    }

    fun arm() = AlertService.arm(context)
    fun cancel() = AlertService.cancel(context)
    fun stopSound() = AlertService.stopRinging(context)

    /**
     * Call when a screen resumes. The alert is armed in preferences but its service is not running:
     * the phone killed it in the background (usually a battery saver) and Android did not restart it.
     */
    fun checkStoppedBySystem() {
        if (AlertPrefs.getArmedAt(context) == 0L ||
            AlertService.armedSince.value != null ||
            AlertService.isRinging.value
        ) return

        if (ConnectionMonitor.snapshot(context).status == InternetStatus.ONLINE) {
            AlertPrefs.clearArmedAt(context)
            _stoppedBySystem.value = StoppedBySystem.MISSED
        } else {
            AlertService.resume(context)
            _stoppedBySystem.value = StoppedBySystem.RESUMED
        }
    }

    /** The explanation is shown once per visit. */
    fun clearStoppedNotice() {
        _stoppedBySystem.value = null
    }

    private companion object {
        const val STOP_DELAY_MS = 2_000L
        const val SIGNAL_POLL_MS = 1_000L
    }
}
