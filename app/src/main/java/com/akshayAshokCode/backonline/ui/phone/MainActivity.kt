package com.akshayAshokCode.backonline.ui.phone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.akshayAshokCode.backonline.alert.AlertService
import com.akshayAshokCode.backonline.ui.common.BackOnlineTheme
import com.akshayAshokCode.backonline.ui.common.HomeViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            BackOnlineTheme {
                PhoneHome(viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Opening the app means the user is back — stop the ring
        AlertService.stopRinging(this)
        viewModel.checkStoppedBySystem()
    }

    override fun onPause() {
        super.onPause()
        viewModel.clearStoppedNotice()
    }
}
