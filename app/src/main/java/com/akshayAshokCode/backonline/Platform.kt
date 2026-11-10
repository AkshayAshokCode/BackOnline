package com.akshayAshokCode.backonline

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import com.akshayAshokCode.backonline.ui.phone.MainActivity
import com.akshayAshokCode.backonline.ui.tv.TvActivity

/** True on Android TV / Google TV (or a phone emulator forced into TV mode in debug builds). */
fun Context.isTelevision(): Boolean =
    getSystemService(UiModeManager::class.java)?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION ||
        DebugFlags.forceTelevision(this)

/** Opens the right home screen for this device: the TV status board or the phone screen. */
fun Context.homeScreenIntent(): Intent =
    Intent(this, if (isTelevision()) TvActivity::class.java else MainActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
