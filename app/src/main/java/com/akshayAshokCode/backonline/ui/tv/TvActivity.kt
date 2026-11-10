package com.akshayAshokCode.backonline.ui.tv

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.akshayAshokCode.backonline.alert.AutoWatchService
import com.akshayAshokCode.backonline.ui.common.HomeViewModel

/**
 * The living-room status board. Unlike the phone, opening this screen does not stop the ring:
 * the board is meant to stay open while waiting, and the user stops the sound with the remote.
 */
class TvActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TvTheme {
                TvBoard(viewModel = viewModel, keepScreenOn = ::setKeepScreenOn)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Allowed now that the app is in the foreground; also covers a watcher stopped by the system
        AutoWatchService.startIfEnabled(this)
        viewModel.checkStoppedBySystem()
    }

    override fun onPause() {
        super.onPause()
        viewModel.clearStoppedNotice()
    }

    /** While waiting or ringing, keep the TV from going to its screensaver or sleeping. */
    private fun setKeepScreenOn(on: Boolean) {
        if (on) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}
