package com.akshayAshokCode.backonline

import android.content.Context

/**
 * Debug builds only: lets a phone emulator behave like a TV, so TV-only features (automatic
 * alerts) can be tested where root is available to simulate outages:
 *
 *   adb shell am broadcast -a com.akshayAshokCode.backonline.debug.FORCE_TV --ez on true -p com.akshayAshokCode.backonline
 */
internal object DebugFlags {
    private const val PREFS = "backonline_debug"
    private const val KEY_FORCE_TV = "force_tv"

    fun forceTelevision(context: Context): Boolean =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_FORCE_TV, false)

    fun setForceTelevision(context: Context, on: Boolean) {
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_FORCE_TV, on).apply()
    }
}
