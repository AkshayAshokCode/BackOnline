package com.akshayAshokCode.backonline.alert

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray

/** One internet outage seen by the TV's automatic watcher. */
data class Outage(val start: Long, val end: Long) {
    val durationMs get() = end - start
}

/** A small local history of outages (newest last), kept on the device only. */
object OutageLog {
    private const val NAME = "backonline_outages"
    private const val KEY = "outages"
    private const val MAX_ENTRIES = 200

    private val _entries = MutableStateFlow<List<Outage>?>(null)

    /** Loads once from storage, then stays in sync with [record]. */
    fun entries(context: Context): StateFlow<List<Outage>?> {
        if (_entries.value == null) _entries.value = load(context)
        return _entries.asStateFlow()
    }

    fun record(context: Context, start: Long, end: Long) {
        val updated = (load(context) + Outage(start, end)).takeLast(MAX_ENTRIES)
        val json = JSONArray().apply {
            updated.forEach { put(JSONArray().put(it.start).put(it.end)) }
        }
        context.applicationContext.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit { putString(KEY, json.toString()) }
        _entries.value = updated
    }

    private fun load(context: Context): List<Outage> {
        val raw = context.applicationContext.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .getString(KEY, null) ?: return emptyList()
        return try {
            val json = JSONArray(raw)
            (0 until json.length()).map { i ->
                val pair = json.getJSONArray(i)
                Outage(pair.getLong(0), pair.getLong(1))
            }
        } catch (_: Exception) {
            emptyList() // corrupt data: start fresh rather than crash
        }
    }
}
