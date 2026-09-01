package com.xabi.simbase.widget

import android.content.Context
import android.content.SharedPreferences

object SimWidgetPrefs {

    private const val PREFS_NAME = "com.xabi.simbase.widget.SimWidgetPrefs"
    private const val PREF_PREFIX_KEY_ICCID = "appwidget_iccid_"
    private const val PREF_PREFIX_KEY_NAME = "appwidget_name_"
    private const val PREF_PREFIX_KEY_STATE = "appwidget_state_"
    private const val PREF_KEY_ALL_WIDGET_IDS = "appwidget_all_ids"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveWidgetData(
        context: Context,
        appWidgetId: Int,
        iccid: String,
        simName: String,
        state: String
    ) {
        val prefs = getPrefs(context)
        val allIds = prefs.getStringSet(PREF_KEY_ALL_WIDGET_IDS, emptySet())?.toMutableSet() ?: mutableSetOf()
        allIds.add(appWidgetId.toString())

        prefs.edit()
            .putString(PREF_PREFIX_KEY_ICCID + appWidgetId, iccid)
            .putString(PREF_PREFIX_KEY_NAME + appWidgetId, simName)
            .putString(PREF_PREFIX_KEY_STATE + appWidgetId, state)
            .putStringSet(PREF_KEY_ALL_WIDGET_IDS, allIds)
            .apply()
    }

    fun updateWidgetState(context: Context, appWidgetId: Int, state: String) {
        val prefs = getPrefs(context)
        prefs.edit()
            .putString(PREF_PREFIX_KEY_STATE + appWidgetId, state)
            .apply()
    }

    fun updateWidgetName(context: Context, appWidgetId: Int, simName: String) {
        val prefs = getPrefs(context)
        prefs.edit()
            .putString(PREF_PREFIX_KEY_NAME + appWidgetId, simName)
            .apply()
    }

    fun getWidgetIccid(context: Context, appWidgetId: Int): String? {
        val prefs = getPrefs(context)
        return prefs.getString(PREF_PREFIX_KEY_ICCID + appWidgetId, null)
    }

    fun getWidgetSimName(context: Context, appWidgetId: Int): String? {
        val prefs = getPrefs(context)
        return prefs.getString(PREF_PREFIX_KEY_NAME + appWidgetId, "SIM")
    }

    fun getWidgetSimState(context: Context, appWidgetId: Int): String? {
        val prefs = getPrefs(context)
        return prefs.getString(PREF_PREFIX_KEY_STATE + appWidgetId, "disabled")
    }

    fun deleteWidgetData(context: Context, appWidgetId: Int) {
        val prefs = getPrefs(context)
        val allIds = prefs.getStringSet(PREF_KEY_ALL_WIDGET_IDS, emptySet())?.toMutableSet() ?: mutableSetOf()
        allIds.remove(appWidgetId.toString())

        prefs.edit()
            .remove(PREF_PREFIX_KEY_ICCID + appWidgetId)
            .remove(PREF_PREFIX_KEY_NAME + appWidgetId)
            .remove(PREF_PREFIX_KEY_STATE + appWidgetId)
            .putStringSet(PREF_KEY_ALL_WIDGET_IDS, allIds)
            .apply()
    }

    fun getAllConfiguredWidgetIds(context: Context): List<Int> {
        val prefs = getPrefs(context)
        val allIds = prefs.getStringSet(PREF_KEY_ALL_WIDGET_IDS, emptySet()) ?: emptySet()
        return allIds.mapNotNull { it.toIntOrNull() }
    }

    fun getWidgetIdsForIccid(context: Context, iccid: String): List<Int> {
        val allIds = getAllConfiguredWidgetIds(context)
        return allIds.filter { id ->
            getWidgetIccid(context, id) == iccid
        }
    }
}
