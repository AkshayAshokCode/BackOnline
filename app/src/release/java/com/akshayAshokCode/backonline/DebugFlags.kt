package com.akshayAshokCode.backonline

import android.content.Context

/** Release builds: no test switches. The debug source set has the real implementation. */
internal object DebugFlags {
    fun forceTelevision(context: Context): Boolean = false
}
