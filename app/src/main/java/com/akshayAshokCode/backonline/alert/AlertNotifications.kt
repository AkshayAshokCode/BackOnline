package com.akshayAshokCode.backonline.alert

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.text.format.DateFormat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.akshayAshokCode.backonline.R
import com.akshayAshokCode.backonline.homeScreenIntent
import com.akshayAshokCode.backonline.monitor.ConnectionMonitor
import com.akshayAshokCode.backonline.monitor.ConnectionState
import com.akshayAshokCode.backonline.monitor.InternetStatus
import com.akshayAshokCode.backonline.monitor.Medium
import com.akshayAshokCode.backonline.monitor.Watched
import java.util.Date

/** Channels and notifications for the "notify me when it is back" alert. */
object AlertNotifications {

    const val ID_WAITING = 1001
    const val ID_RINGING = 1003
    const val ID_WATCHING = 1004
    private const val ID_RESULT = 1002

    private const val CHANNEL_WAITING = "waiting"
    private const val CHANNEL_ALERT   = "back"
    private const val CHANNEL_RINGING = "ringing" // silent: the service plays the alarm sound
    private const val CHANNEL_WATCHING = "watching" // TV automatic alerts: always-on, silent

    private const val ACCENT = 0xFF1E9E6A.toInt() // brand green

    fun createChannels(context: Context) {
        val manager = NotificationManagerCompat.from(context)
        manager.createNotificationChannel(
            NotificationChannelCompat.Builder(CHANNEL_WAITING, NotificationManagerCompat.IMPORTANCE_LOW)
                .setName(context.getString(R.string.channel_waiting))
                .setDescription(context.getString(R.string.channel_waiting_desc))
                .setShowBadge(false)
                .build()
        )
        manager.createNotificationChannel(
            NotificationChannelCompat.Builder(CHANNEL_ALERT, NotificationManagerCompat.IMPORTANCE_HIGH)
                .setName(context.getString(R.string.channel_alert))
                .setDescription(context.getString(R.string.channel_alert_desc))
                .setVibrationEnabled(true)
                .setVibrationPattern(longArrayOf(0, 400, 200, 400))
                .build()
        )
        manager.createNotificationChannel(
            NotificationChannelCompat.Builder(CHANNEL_RINGING, NotificationManagerCompat.IMPORTANCE_HIGH)
                .setName(context.getString(R.string.channel_ringing))
                .setDescription(context.getString(R.string.channel_ringing_desc))
                .setSound(null, null)
                .setVibrationEnabled(false)
                .build()
        )
        manager.createNotificationChannel(
            NotificationChannelCompat.Builder(CHANNEL_WATCHING, NotificationManagerCompat.IMPORTANCE_MIN)
                .setName(context.getString(R.string.channel_watching))
                .setDescription(context.getString(R.string.channel_watching_desc))
                .setShowBadge(false)
                .build()
        )
    }

    /** Foreground notification for the TV's automatic watcher (TVs barely show it). */
    fun watching(context: Context): Notification =
        NotificationCompat.Builder(context, CHANNEL_WATCHING)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ACCENT)
            .setContentTitle(context.getString(R.string.notif_watching_title))
            .setContentText(context.getString(R.string.notif_watching_text))
            .setOngoing(true)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(openApp(context))
            .build()

    /** Ongoing notification shown while waiting — visible on the lock screen so a glance is enough. */
    fun waiting(
        context      : Context,
        armedAt      : Long,
        state        : ConnectionState,
        lineIndex    : Int,
        isLongOutage : Boolean
    ): Notification {
        val lines = context.resources.getStringArray(
            if (isLongOutage) R.array.long_outage_lines else R.array.waiting_lines
        )
        val line = lines[lineIndex % lines.size]
        val statusText = context.getString(waitingStatusRes(state, ConnectionMonitor.isWifiEnabled(context)))
        return NotificationCompat.Builder(context, CHANNEL_WAITING)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ACCENT)
            .setContentTitle(context.getString(R.string.notif_waiting_title, formatTime(context, armedAt)))
            .setContentText(statusText)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$statusText\n\n$line"))
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setContentIntent(openApp(context))
            .addAction(0, context.getString(R.string.notif_cancel), AlertService.cancelPendingIntent(context))
            .build()
    }

    private fun waitingStatusRes(state: ConnectionState, isWifiOn: Boolean): Int = when {
        state.status == InternetStatus.SIGN_IN_NEEDED -> R.string.notif_status_sign_in
        state.watched == Watched.MOBILE && state.status == InternetStatus.NO_INTERNET -> R.string.notif_status_no_internet_mobile
        state.watched == Watched.MOBILE -> R.string.notif_status_disconnected_mobile
        state.status == InternetStatus.NO_INTERNET && state.medium == Medium.ETHERNET -> R.string.notif_status_no_internet_ethernet
        state.status == InternetStatus.NO_INTERNET -> R.string.notif_status_no_internet_wifi
        isWifiOn -> R.string.notif_status_disconnected_wifi
        else -> R.string.notif_status_disconnected_home
    }

    /** Foreground notification while ringing: heads-up, with a Stop button. Swiping it away also stops the ring. */
    fun ringing(context: Context, waited: String, line: String): Notification =
        NotificationCompat.Builder(context, CHANNEL_RINGING)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ACCENT)
            .setContentTitle(context.getString(R.string.notif_back_title))
            .setContentText(waited)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$waited\n\n$line"))
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS) // not CATEGORY_ALARM: that would bypass Do Not Disturb
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setContentIntent(openApp(context))
            .setDeleteIntent(AlertService.stopRingPendingIntent(context))
            .addAction(0, context.getString(R.string.notif_stop), AlertService.stopRingPendingIntent(context))
            .build()

    /** The "internet is back" notification. [silent] is used after a ring, which already made the noise. */
    fun showBack(context: Context, waited: String, line: String, silent: Boolean) {
        notify(
            context,
            NotificationCompat.Builder(context, if (silent) CHANNEL_WAITING else CHANNEL_ALERT)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ACCENT)
                .setContentTitle(context.getString(R.string.notif_back_title))
                .setContentText(waited)
                .setStyle(NotificationCompat.BigTextStyle().bigText("$waited\n\n$line"))
                .setPriority(if (silent) NotificationCompat.PRIORITY_LOW else NotificationCompat.PRIORITY_HIGH)
                .setDefaults(if (silent) 0 else NotificationCompat.DEFAULT_ALL) // sound + vibration below API 26
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setContentIntent(openApp(context))
                .build()
        )
    }

    fun showTimedOut(context: Context, hours: Int) {
        val text = context.getString(
            R.string.notif_timeout_text,
            context.resources.getQuantityString(R.plurals.hours, hours, hours)
        )
        notify(
            context,
            NotificationCompat.Builder(context, CHANNEL_WAITING)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ACCENT)
                .setContentTitle(context.getString(R.string.notif_stopped_title))
                .setContentText(text)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setAutoCancel(true)
                .setContentIntent(openApp(context))
                .build()
        )
    }

    fun showStoppedBySystem(context: Context) {
        val text = context.getString(R.string.notif_stopped_text)
        notify(
            context,
            NotificationCompat.Builder(context, CHANNEL_ALERT)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ACCENT)
                .setContentTitle(context.getString(R.string.notif_stopped_title))
                .setContentText(text)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(openApp(context))
                .build()
        )
    }

    fun update(context: Context, notification: Notification) {
        notify(context, notification, ID_WAITING)
    }

    /**
     * "Back after 42 minutes" — counted from when the user armed the alert, which is the wait
     * they experienced (the outage itself may have started earlier).
     */
    fun backAfter(context: Context, waitedMs: Long): String =
        context.getString(R.string.back_after, duration(context, waitedMs))

    fun duration(context: Context, millis: Long): String {
        val res = context.resources
        val totalMinutes = (millis / 60_000L).toInt()
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return when {
            totalMinutes < 1 -> res.getString(R.string.less_than_a_minute)
            hours == 0 -> res.getQuantityString(R.plurals.minutes, minutes, minutes)
            minutes == 0 -> res.getQuantityString(R.plurals.hours, hours, hours)
            else -> res.getString(
                R.string.hours_and_minutes,
                res.getQuantityString(R.plurals.hours, hours, hours),
                res.getQuantityString(R.plurals.minutes, minutes, minutes)
            )
        }
    }

    fun randomBackLine(context: Context): String =
        context.resources.getStringArray(R.array.back_lines).random()

    fun formatTime(context: Context, millis: Long): String =
        DateFormat.getTimeFormat(context).format(Date(millis))

    /** Opens the right home screen for this device (phone screen or TV board). */
    fun openApp(context: Context): PendingIntent = PendingIntent.getActivity(
        context, 0, context.homeScreenIntent(),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    @SuppressLint("MissingPermission") // checked by areNotificationsEnabled()
    private fun notify(context: Context, notification: Notification, id: Int = ID_RESULT) {
        val manager = NotificationManagerCompat.from(context)
        if (manager.areNotificationsEnabled()) manager.notify(id, notification)
    }
}
