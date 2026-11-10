package com.akshayAshokCode.backonline

import android.app.Application
import com.akshayAshokCode.backonline.alert.AlertNotifications

class BackOnlineApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AlertNotifications.createChannels(this)
    }
}
