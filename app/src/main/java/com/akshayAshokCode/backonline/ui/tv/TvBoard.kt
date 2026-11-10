package com.akshayAshokCode.backonline.ui.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.darkColorScheme
import com.akshayAshokCode.backonline.R
import com.akshayAshokCode.backonline.alert.AlertNotifications
import com.akshayAshokCode.backonline.alert.Outage
import com.akshayAshokCode.backonline.monitor.Blocker
import com.akshayAshokCode.backonline.monitor.ConnectionMonitor
import com.akshayAshokCode.backonline.monitor.InternetStatus
import com.akshayAshokCode.backonline.ui.common.BrandColors
import com.akshayAshokCode.backonline.ui.common.HomeViewModel
import com.akshayAshokCode.backonline.ui.common.PulseMark
import com.akshayAshokCode.backonline.ui.common.StoppedBySystem
import com.akshayAshokCode.backonline.ui.common.SystemSettings
import com.akshayAshokCode.backonline.ui.common.connectionLine
import com.akshayAshokCode.backonline.ui.common.statusColor
import com.akshayAshokCode.backonline.ui.common.statusTitle

/** "Back online" stays the headline for this long after the alert fired. */
private const val RECENTLY_BACK_MS = 30 * 60 * 1000L

private val TvColors = darkColorScheme(
    primary = BrandColors.Green,
    onPrimary = Color.White,
    background = Color(0xFF0B100E),
    onBackground = BrandColors.DarkText,
    surface = BrandColors.DarkSurface,
    onSurface = BrandColors.DarkText,
    onSurfaceVariant = BrandColors.DarkTextMuted
)

@Composable
fun TvTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = TvColors, content = content)
}

/**
 * Full-screen board, readable from the sofa (~3 m). Remote (D-pad) only: one focused button.
 * Content stays inside the TV safe area (48 dp sides, 27 dp top/bottom).
 */
@Composable
fun TvBoard(viewModel: HomeViewModel, keepScreenOn: (Boolean) -> Unit) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val signal by viewModel.signal.collectAsStateWithLifecycle()
    val armedSince by viewModel.armedSince.collectAsStateWithLifecycle()
    val isRinging by viewModel.isRinging.collectAsStateWithLifecycle()
    val lastBack by viewModel.lastBack.collectAsStateWithLifecycle()
    val stoppedBySystem by viewModel.stoppedBySystem.collectAsStateWithLifecycle()
    val autoWatch by viewModel.autoWatch.collectAsStateWithLifecycle()
    val outages by viewModel.outages.collectAsStateWithLifecycle()

    val isArmed = armedSince != null
    val isOnline = state.blocker == null && state.status == InternetStatus.ONLINE
    val back = lastBack
    val recentlyBack = isOnline && back != null && System.currentTimeMillis() - back.at < RECENTLY_BACK_MS

    LaunchedEffect(isArmed, isRinging) { keepScreenOn(isArmed || isRinging) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 48.dp, vertical = 27.dp)
    ) {
        if (!ConnectionMonitor.isSupported(context)) {
            Text(
                text = stringResource(R.string.not_supported),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(Alignment.Center)
            )
            return@Box
        }

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.TopStart)
        )

        // Mark and text share the width, so the board fits any TV (and narrow test screens)
        Row(
            modifier = Modifier.align(Alignment.Center).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PulseMark(
                color = if (isRinging) BrandColors.Green else statusColor(state),
                waiting = isArmed,
                modifier = Modifier.weight(0.35f).aspectRatio(1f)
            )
            Spacer(Modifier.width(56.dp))
            Column(modifier = Modifier.weight(0.65f)) {
                Text(
                    text = if (isRinging) stringResource(R.string.status_internet_back) else statusTitle(state, recentlyBack),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                val caption = when {
                    isRinging || recentlyBack -> back?.let { AlertNotifications.backAfter(context, it.waitedMs) }
                    isArmed -> stringResource(R.string.armed_since, AlertNotifications.formatTime(context, armedSince!!))
                    else -> connectionLine(state, signal)
                }
                if (caption != null) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = caption,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(24.dp))
                val message = when {
                    stoppedBySystem == StoppedBySystem.RESUMED -> stringResource(R.string.stopped_resumed)
                    stoppedBySystem == StoppedBySystem.MISSED -> stringResource(R.string.stopped_missed)
                    isRinging -> null
                    isArmed -> stringResource(R.string.tv_hint_armed)
                    state.blocker == Blocker.WIFI_OFF -> stringResource(R.string.wifi_off_hint)
                    state.status == InternetStatus.SIGN_IN_NEEDED -> stringResource(R.string.sign_in_hint)
                    isOnline -> stringResource(if (autoWatch) R.string.auto_hint_on else R.string.auto_hint_off)
                    autoWatch -> stringResource(R.string.auto_hint_on)
                    else -> stringResource(R.string.tv_hint_idle)
                }
                if (message != null) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (autoWatch && !isRinging) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = weekSummary(outages.orEmpty()),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.height(32.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    when {
                        isRinging -> BoardButton(stringResource(R.string.button_stop_sound), key = "stop") {
                            viewModel.stopSound()
                        }
                        isArmed -> BoardButton(stringResource(R.string.button_cancel), key = "cancel") {
                            viewModel.cancel()
                        }
                        state.blocker == Blocker.WIFI_OFF -> BoardButton(stringResource(R.string.button_wifi_settings), key = "wifi") {
                            SystemSettings.openWifi(context)
                        }
                        !isOnline -> {
                            BoardButton(stringResource(R.string.button_arm), key = "arm") {
                                // On TV the alert always rings: notifications are barely visible there
                                viewModel.setRingMode(true)
                                viewModel.arm()
                            }
                            if (state.status == InternetStatus.SIGN_IN_NEEDED) {
                                BoardButton(stringResource(R.string.button_sign_in), key = "signin", requestFocus = false) {
                                    SystemSettings.openWifiList(context)
                                }
                            }
                        }
                    }
                    if (!isRinging) {
                        // The only button when online, so it takes focus then
                        BoardButton(
                            text = stringResource(if (autoWatch) R.string.auto_on else R.string.auto_off),
                            key = "auto",
                            requestFocus = isOnline && !isArmed,
                            primary = false
                        ) { viewModel.setAutoWatch(!autoWatch) }
                    }
                }
            }
        }
    }
}

/** "Last 7 days: 3 outages · 2 hours 14 minutes", or "No outages in the last 7 days." */
@Composable
private fun weekSummary(outages: List<Outage>): String {
    val context = LocalContext.current
    val weekAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
    val recent = outages.filter { it.end >= weekAgo }
    if (recent.isEmpty()) return stringResource(R.string.history_none)
    return stringResource(
        R.string.history_week,
        context.resources.getQuantityString(R.plurals.outages, recent.size, recent.size),
        AlertNotifications.duration(context, recent.sumOf { it.durationMs })
    )
}

/** A remote-friendly button; the main one takes focus as soon as it appears. */
@Composable
private fun BoardButton(
    text: String,
    key: String,
    requestFocus: Boolean = true,
    primary: Boolean = true,
    onClick: () -> Unit
) {
    val focusRequester = remember(key) { FocusRequester() }
    Button(
        onClick = onClick,
        modifier = Modifier.focusRequester(focusRequester),
        colors = ButtonDefaults.colors(
            containerColor = if (primary) BrandColors.Green.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f),
            contentColor = Color.White,
            focusedContainerColor = BrandColors.Green,
            focusedContentColor = Color.White
        )
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
    if (requestFocus) {
        LaunchedEffect(key) {
            try {
                focusRequester.requestFocus()
            } catch (_: IllegalStateException) {
                // Not laid out yet — the user can still move focus with the remote
            }
        }
    }
}
