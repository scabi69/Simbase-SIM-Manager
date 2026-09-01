package com.xabi.simbase.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import com.xabi.simbase.MainActivity
import com.xabi.simbase.R
import com.xabi.simbase.api.ApiClient
import com.xabi.simbase.api.SimStateRequest
import com.xabi.simbase.data.TokenStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SimWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_TOGGLE_SIM = "com.xabi.simbase.widget.ACTION_TOGGLE_SIM"
        const val EXTRA_ICCID = "extra_iccid"
        private const val TAG = "SimWidgetProvider"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val iccid = SimWidgetPrefs.getWidgetIccid(context, appWidgetId)
            if (iccid.isNullOrBlank()) {
                Log.w(TAG, "Widget $appWidgetId no tiene ICCID asociado")
                return
            }

            val simName = SimWidgetPrefs.getWidgetSimName(context, appWidgetId) ?: "SIM"
            val state = SimWidgetPrefs.getWidgetSimState(context, appWidgetId) ?: "disabled"
            val stateLower = state.lowercase()

            val views = RemoteViews(context.packageName, R.layout.widget_sim_1x1)

            // Nombre de la SIM
            views.setTextViewText(R.id.widget_sim_name, simName.ifBlank { "SIM" })

            when (stateLower) {
                "enabled" -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.widget_background_enabled)
                    views.setInt(R.id.widget_status_badge, "setBackgroundResource", R.drawable.widget_badge_enabled)
                    views.setTextViewText(R.id.widget_status_badge, "ACTIVA")
                    views.setTextColor(R.id.widget_status_badge, Color.parseColor("#2E7D32"))
                    views.setImageViewResource(R.id.widget_btn_toggle, R.drawable.ic_widget_toggle_on)
                    views.setViewVisibility(R.id.widget_btn_toggle, View.VISIBLE)
                    views.setViewVisibility(R.id.widget_progress, View.GONE)
                }
                "disabled" -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.widget_background_disabled)
                    views.setInt(R.id.widget_status_badge, "setBackgroundResource", R.drawable.widget_badge_disabled)
                    views.setTextViewText(R.id.widget_status_badge, "DESACT.")
                    views.setTextColor(R.id.widget_status_badge, Color.parseColor("#C62828"))
                    views.setImageViewResource(R.id.widget_btn_toggle, R.drawable.ic_widget_toggle_off)
                    views.setViewVisibility(R.id.widget_btn_toggle, View.VISIBLE)
                    views.setViewVisibility(R.id.widget_progress, View.GONE)
                }
                "enabling", "disabling" -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.widget_background_transition)
                    views.setInt(R.id.widget_status_badge, "setBackgroundResource", R.drawable.widget_badge_transition)
                    val label = if (stateLower == "enabling") "ACTIVANDO" else "DESACTIV."
                    views.setTextViewText(R.id.widget_status_badge, label)
                    views.setTextColor(R.id.widget_status_badge, Color.parseColor("#EF6C00"))
                    views.setViewVisibility(R.id.widget_btn_toggle, View.GONE)
                    views.setViewVisibility(R.id.widget_progress, View.VISIBLE)
                }
                else -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.widget_background_neutral)
                    views.setInt(R.id.widget_status_badge, "setBackgroundResource", R.drawable.widget_badge_disabled)
                    views.setTextViewText(R.id.widget_status_badge, state.uppercase())
                    views.setTextColor(R.id.widget_status_badge, Color.parseColor("#757575"))
                    views.setImageViewResource(R.id.widget_btn_toggle, R.drawable.ic_widget_toggle_off)
                    views.setViewVisibility(R.id.widget_btn_toggle, View.VISIBLE)
                    views.setViewVisibility(R.id.widget_progress, View.GONE)
                }
            }

            // PendingIntent para cambiar de estado al pulsar el interruptor
            val toggleIntent = Intent(context, SimWidgetProvider::class.java).apply {
                action = ACTION_TOGGLE_SIM
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                putExtra(EXTRA_ICCID, iccid)
            }
            val togglePendingIntent = PendingIntent.getBroadcast(
                context,
                appWidgetId,
                toggleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_toggle_container, togglePendingIntent)
            views.setOnClickPendingIntent(R.id.widget_btn_toggle, togglePendingIntent)

            // PendingIntent para abrir la App al pulsar el nombre de la SIM
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val openAppPendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId + 100000,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_sim_name, openAppPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val allIds = SimWidgetPrefs.getAllConfiguredWidgetIds(context)
            for (id in allIds) {
                updateAppWidget(context, appWidgetManager, id)
            }
        }

        fun updateWidgetsForSim(context: Context, iccid: String, state: String?, name: String? = null) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val widgetIds = SimWidgetPrefs.getWidgetIdsForIccid(context, iccid)
            for (id in widgetIds) {
                state?.let { SimWidgetPrefs.updateWidgetState(context, id, it) }
                name?.let { SimWidgetPrefs.updateWidgetName(context, id, it) }
                updateAppWidget(context, appWidgetManager, id)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            SimWidgetPrefs.deleteWidgetData(context, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == ACTION_TOGGLE_SIM) {
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            val iccid = intent.getStringExtra(EXTRA_ICCID)
                ?: if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    SimWidgetPrefs.getWidgetIccid(context, appWidgetId)
                } else null

            if (iccid.isNullOrBlank()) {
                Log.w(TAG, "ACTION_TOGGLE_SIM sin ICCID válido")
                return
            }

            val currentState = if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                SimWidgetPrefs.getWidgetSimState(context, appWidgetId)
            } else "disabled"

            // Evitar re-petición si ya está en transición
            if (currentState?.lowercase() in listOf("enabling", "disabling")) {
                Log.d(TAG, "SIM ya en transición: $iccid")
                return
            }

            val targetState = if (currentState?.lowercase() == "enabled") "disabled" else "enabled"
            val transitionState = if (targetState == "enabled") "enabling" else "disabling"

            // Actualización visual inmediata de estado en transición
            updateWidgetsForSim(context, iccid, transitionState)

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val tokens = TokenStore.readTokens(context).first()
                    val readToken = tokens.first.trim()
                    val writeToken = tokens.second.trim()

                    if (writeToken.isBlank()) {
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(
                                context,
                                "Token de escritura no configurado en la app",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        updateWidgetsForSim(context, iccid, currentState)
                        return@launch
                    }

                    // Invocación a la API para cambiar estado
                    ApiClient.api.setSimState(
                        iccid = iccid,
                        token = writeToken,
                        body = SimStateRequest(targetState)
                    )

                    // Delay según operación (apagar ~11s, encender ~17s)
                    val delayMs = if (targetState == "disabled") 11_000L else 17_000L
                    delay(delayMs)

                    // Confirmar estado final desde la API
                    var finalState = targetState
                    var finalName: String? = null

                    if (readToken.isNotBlank()) {
                        try {
                            val response = ApiClient.api.getAllSims(readToken)
                            val sim = response.simcards?.find { it.iccid == iccid }
                            if (sim != null) {
                                finalState = sim.state ?: targetState
                                finalName = sim.name
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error confirmando estado final", e)
                        }
                    }

                    updateWidgetsForSim(context, iccid, finalState, finalName)

                } catch (e: Exception) {
                    Log.e(TAG, "Error modificando SIM desde widget", e)
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            context,
                            "Error al cambiar estado de la SIM: ${e.localizedMessage ?: e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    // Restaurar estado anterior
                    updateWidgetsForSim(context, iccid, currentState)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
