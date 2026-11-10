package com.akshayAshokCode.backonline.ui.phone

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akshayAshokCode.backonline.R
import com.akshayAshokCode.backonline.alert.AlertNotifications
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
import kotlinx.coroutines.delay

/** "Back online" stays the headline for this long after the alert fired. */
private const val RECENTLY_BACK_MS = 30 * 60 * 1000L

@Composable
fun PhoneHome(viewModel: HomeViewModel) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val signal by viewModel.signal.collectAsStateWithLifecycle()
    val armedSince by viewModel.armedSince.collectAsStateWithLifecycle()
    val lastBack by viewModel.lastBack.collectAsStateWithLifecycle()
    val ringMode by viewModel.ringMode.collectAsStateWithLifecycle()
    val stoppedBySystem by viewModel.stoppedBySystem.collectAsStateWithLifecycle()

    var notificationsBlocked by remember { mutableStateOf(false) }
    LifecycleResumeEffect(Unit) {
        // Returning from Settings after allowing notifications
        if (notificationsBlocked && NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            notificationsBlocked = false
        }
        onPauseOrDispose { }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) viewModel.arm() else notificationsBlocked = true
    }
    val onArm = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            notificationsBlocked = true
        } else {
            notificationsBlocked = false
            viewModel.arm()
        }
    }

    val isArmed = armedSince != null
    val isOnline = state.blocker == null && state.status == InternetStatus.ONLINE
    val back = lastBack
    val recentlyBack = isOnline && back != null && System.currentTimeMillis() - back.at < RECENTLY_BACK_MS
    val color = statusColor(state)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        if (!ConnectionMonitor.isSupported(context)) {
            Text(
                text = stringResource(R.string.not_supported),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center).padding(32.dp)
            )
            return@BoxWithConstraints
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = maxHeight)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PulseMark(color = color, waiting = isArmed, modifier = Modifier.size(220.dp))
                Spacer(Modifier.height(20.dp))
                Text(
                    text = statusTitle(state, recentlyBack),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                val caption = when {
                    isArmed -> stringResource(R.string.armed_since, AlertNotifications.formatTime(context, armedSince!!))
                    recentlyBack -> AlertNotifications.backAfter(context, back!!.waitedMs)
                    else -> connectionLine(state, signal)
                }
                if (caption != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = caption,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Message(
                isArmed = isArmed,
                isOnline = isOnline,
                ringMode = ringMode,
                blocker = state.blocker,
                signInNeeded = state.status == InternetStatus.SIGN_IN_NEEDED,
                notificationsBlocked = notificationsBlocked,
                stoppedBySystem = stoppedBySystem
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when {
                    isArmed -> SecondaryButton(stringResource(R.string.button_cancel)) { viewModel.cancel() }
                    state.blocker == Blocker.WIFI_OFF ->
                        PrimaryButton(stringResource(R.string.button_wifi_settings)) { SystemSettings.openWifi(context) }
                    state.blocker == Blocker.AIRPLANE_MODE ->
                        PrimaryButton(stringResource(R.string.button_airplane_settings)) { SystemSettings.openAirplaneMode(context) }
                    isOnline -> PrimaryButton(
                        text = "✓  " + stringResource(R.string.button_online),
                        enabled = false
                    ) {}
                    notificationsBlocked ->
                        PrimaryButton(stringResource(R.string.button_notification_settings)) { SystemSettings.openNotifications(context) }
                    else -> {
                        PrimaryButton(stringResource(R.string.button_arm), onClick = onArm)
                        StyleToggle(ringMode = ringMode, onChange = viewModel::setRingMode)
                    }
                }
                if (!isArmed && state.status == InternetStatus.SIGN_IN_NEEDED) {
                    TextButton(onClick = { SystemSettings.openWifiList(context) }) {
                        Text(stringResource(R.string.button_sign_in))
                    }
                }
                if (stoppedBySystem != null) {
                    TextButton(onClick = { SystemSettings.openAppInfo(context) }) {
                        Text(stringResource(R.string.button_app_info))
                    }
                }
            }
        }
    }
}

@Composable
private fun Message(
    isArmed: Boolean,
    isOnline: Boolean,
    ringMode: Boolean,
    blocker: Blocker?,
    signInNeeded: Boolean,
    notificationsBlocked: Boolean,
    stoppedBySystem: StoppedBySystem?
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when {
            stoppedBySystem != null -> {
                Body(
                    stringResource(
                        if (stoppedBySystem == StoppedBySystem.RESUMED) R.string.stopped_resumed
                        else R.string.stopped_missed
                    ),
                    BrandColors.Amber
                )
                Body(stringResource(R.string.battery_hint))
            }
            isArmed -> {
                Title(stringResource(R.string.armed_title), BrandColors.Green)
                RotatingLine()
                Body(
                    stringResource(
                        if (ringMode) R.string.armed_style_ring else R.string.armed_style_notification
                    )
                )
            }
            blocker == Blocker.WIFI_OFF -> Body(stringResource(R.string.wifi_off_hint))
            blocker == Blocker.AIRPLANE_MODE -> Body(stringResource(R.string.airplane_hint))
            isOnline -> Body(stringResource(R.string.online_hint))
            notificationsBlocked -> Body(stringResource(R.string.notifications_off_hint), BrandColors.Amber)
            else -> {
                if (signInNeeded) Body(stringResource(R.string.sign_in_hint))
                Title(stringResource(R.string.headline), MaterialTheme.colorScheme.onBackground)
                Body(stringResource(R.string.trust_1))
                Body(stringResource(R.string.trust_2))
            }
        }
    }
}

/** Cycles the friendly "take a break" lines while waiting. */
@Composable
private fun RotatingLine() {
    val lines = stringArrayResource(R.array.waiting_lines)
    var index by remember { mutableIntStateOf(0) }
    LaunchedEffect(lines.size) {
        while (true) {
            delay(8_000)
            index = (index + 1) % lines.size
        }
    }
    Crossfade(targetState = lines[index % lines.size], label = "line") { Body(it) }
}

@Composable
private fun Title(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = color,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Body(text: String, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = color,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PrimaryButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = BrandColors.Green.copy(alpha = 0.15f),
            disabledContentColor = BrandColors.Green
        ),
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun SecondaryButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

/** Ring / Notification, chosen at the moment of arming. Ring is the default. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StyleToggle(ringMode: Boolean, onChange: (Boolean) -> Unit) {
    val colors = SegmentedButtonDefaults.colors(
        activeContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
        activeContentColor = MaterialTheme.colorScheme.primary,
        activeBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        inactiveBorderColor = MaterialTheme.colorScheme.outline
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = ringMode,
                onClick = { onChange(true) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                colors = colors
            ) { Text("🔔  " + stringResource(R.string.style_ring)) }
            SegmentedButton(
                selected = !ringMode,
                onClick = { onChange(false) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                colors = colors
            ) { Text("🔕  " + stringResource(R.string.style_notification)) }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(if (ringMode) R.string.style_ring_hint else R.string.style_notification_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
