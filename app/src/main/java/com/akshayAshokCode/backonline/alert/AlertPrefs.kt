package com.akshayAshokCode.backonline.alert

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/** Small persistent state for the alert. */
object AlertPrefs {
    private const val NAME = "backonline_prefs"
    private const val KEY_ARMED_AT = "armed_at"
    private const val KEY_RING = "ring"
    private const val KEY_AUTO_WATCH = "auto_watch"

    private fun prefs(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    /** When the alert was armed, so a restart (process kill, reboot) resumes the same wait. 0 = not armed. */
    fun getArmedAt(context: Context): Long = prefs(context).getLong(KEY_ARMED_AT, 0L)

    fun setArmedAt(context: Context, armedAt: Long) {
        prefs(context).edit { putLong(KEY_ARMED_AT, armedAt) }
    }

    fun clearArmedAt(context: Context) {
        prefs(context).edit { remove(KEY_ARMED_AT) }
    }

    /** Alert style: true = ring (default), false = notification. */
    fun isRing(context: Context): Boolean = prefs(context).getBoolean(KEY_RING, true)

    fun setRing(context: Context, ring: Boolean) {
        prefs(context).edit { putBoolean(KEY_RING, ring) }
    }

    /** TV only: watch the connection all the time and alert after every outage. On by default. */
    fun isAutoWatch(context: Context): Boolean = prefs(context).getBoolean(KEY_AUTO_WATCH, true)

    fun setAutoWatch(context: Context, on: Boolean) {
        prefs(context).edit { putBoolean(KEY_AUTO_WATCH, on) }
    }
}
