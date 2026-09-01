package com.xabi.simbase

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xabi.simbase.api.ApiClient
import com.xabi.simbase.api.SimCard
import com.xabi.simbase.api.SimStateRequest
import com.xabi.simbase.data.TokenStore
import com.xabi.simbase.widget.SimWidgetProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SimViewModel(
    private val dataStore: DataStore<Preferences>,
    private val context: Context? = null
) : ViewModel() {

    private val _sims = MutableStateFlow<List<SimCard>>(emptyList())
    val sims = _sims.asStateFlow()

    private val _readToken = MutableStateFlow("")
    val readToken = _readToken.asStateFlow()

    private val _writeToken = MutableStateFlow("")
    val writeToken = _writeToken.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _changingSimIccid = MutableStateFlow<String?>(null)
    val changingSimIccid = _changingSimIccid.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _selectedSim = MutableStateFlow<SimCard?>(null)
    val selectedSim = _selectedSim.asStateFlow()

    init {
        Log.d("VM", "SimViewModel inicializado")

        viewModelScope.launch {
            dataStore.data.collect { prefs ->
                _readToken.value = prefs[TokenStore.READ_TOKEN] ?: ""
                _writeToken.value = prefs[TokenStore.WRITE_TOKEN] ?: ""
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun loadSims() {
        viewModelScope.launch {
            val token = _readToken.value.trim()
            if (token.isBlank()) {
                Log.e("Simbase", "Token de lectura vacío")
                return@launch
            }

            _isLoading.value = true
            try {
                val response = ApiClient.api.getAllSims(token)
                val loadedSims = response.simcards ?: emptyList()
                _sims.value = loadedSims

                // Sincronizar widgets existentes con los nuevos datos
                context?.let { ctx ->
                    for (sim in loadedSims) {
                        SimWidgetProvider.updateWidgetsForSim(
                            context = ctx,
                            iccid = sim.iccid,
                            state = sim.state,
                            name = sim.name
                        )
                    }
                }
            } catch (e: HttpException) {
                val errorMsg = when (e.code()) {
                    401 -> "Token de lectura inválido o no autorizado (401)"
                    403 -> "Permiso denegado con el token de lectura (403)"
                    else -> "Error en la API de Simbase (${e.code()}): ${e.message()}"
                }
                Log.e("Simbase", errorMsg, e)
                _errorMessage.value = errorMsg
            } catch (e: Exception) {
                val errorMsg = "Error de conexión con Simbase: ${e.localizedMessage ?: e.message}"
                Log.e("Simbase", errorMsg, e)
                _errorMessage.value = errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleSim(sim: SimCard) {
        if (_changingSimIccid.value != null) return

        viewModelScope.launch {
            val token = _writeToken.value.trim()
            if (token.isBlank()) {
                _errorMessage.value = "Token de escritura vacío. Configúralo en Ajustes."
                Log.e("Simbase", "Token de escritura vacío")
                return@launch
            }

            val newState = if (sim.state?.lowercase() == "enabled") "disabled" else "enabled"
            _changingSimIccid.value = sim.iccid

            // Notificar a los widgets el inicio de la transición
            context?.let { ctx ->
                val transitionState = if (newState == "enabled") "enabling" else "disabling"
                SimWidgetProvider.updateWidgetsForSim(ctx, sim.iccid, transitionState, sim.name)
            }

            try {
                ApiClient.api.setSimState(
                    sim.iccid,
                    token,
                    SimStateRequest(newState)
                )

                // Recarga inmediata para reflejar estado intermedio si lo reporta la API
                loadSims()

                // Delay según la operación (desactivar suele tardar ~11s, activar ~17s)
                val delayMs = if (newState == "disabled") 11_000L else 17_000L
                delay(delayMs)

                // Recarga final de confirmación
                loadSims()
            } catch (e: HttpException) {
                val errorMsg = when (e.code()) {
                    401 -> "Token de escritura inválido (401)"
                    403 -> "El token de escritura no tiene permisos para cambiar el estado (403)"
                    else -> "Error modificando SIM (${e.code()}): ${e.message()}"
                }
                Log.e("Simbase", errorMsg, e)
                _errorMessage.value = errorMsg
                // Restaurar estado en widgets
                context?.let { ctx ->
                    SimWidgetProvider.updateWidgetsForSim(ctx, sim.iccid, sim.state, sim.name)
                }
            } catch (e: Exception) {
                val errorMsg = "Error modificando SIM: ${e.localizedMessage ?: e.message}"
                Log.e("Simbase", errorMsg, e)
                _errorMessage.value = errorMsg
                // Restaurar estado en widgets
                context?.let { ctx ->
                    SimWidgetProvider.updateWidgetsForSim(ctx, sim.iccid, sim.state, sim.name)
                }
            } finally {
                _changingSimIccid.value = null
            }
        }
    }

    fun updateReadToken(value: String) {
        _readToken.value = value
    }

    fun updateWriteToken(value: String) {
        _writeToken.value = value
    }

    fun saveTokens(onSaved: () -> Unit = {}) {
        Log.d("VM", "Guardando tokens desde ViewModel")
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[TokenStore.READ_TOKEN] = _readToken.value.trim()
                prefs[TokenStore.WRITE_TOKEN] = _writeToken.value.trim()
            }
            onSaved()
        }
    }

    fun clearTokens(onCleared: () -> Unit) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[TokenStore.READ_TOKEN] = ""
                prefs[TokenStore.WRITE_TOKEN] = ""
            }
            _sims.value = emptyList()
            Log.d("CLEAR", "Borrando tokens…")
            onCleared()
        }
    }

    fun selectSim(sim: SimCard) {
        _selectedSim.value = sim
    }

    fun clearSelectedSim() {
        _selectedSim.value = null
    }
}