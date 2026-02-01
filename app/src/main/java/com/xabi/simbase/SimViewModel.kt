package com.xabi.simbase


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xabi.simbase.api.ApiClient
import com.xabi.simbase.api.SimCard
import com.xabi.simbase.api.SimStateRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.datastore.preferences.core.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey


class SimViewModel(
    private val dataStore: DataStore<Preferences>


) : ViewModel()
 {

    private val _sims = MutableStateFlow<List<SimCard>>(emptyList())
    val sims = _sims.asStateFlow()

    private val _readToken = MutableStateFlow("")
    val readToken = _readToken.asStateFlow()

    private val _writeToken = MutableStateFlow("")
    val writeToken = _writeToken.asStateFlow()
     private val READ_TOKEN_KEY = stringPreferencesKey("read_token")
     private val WRITE_TOKEN_KEY = stringPreferencesKey("write_token")
     init {
         Log.d("VM", "SimViewModel inicializado")

         viewModelScope.launch {
             dataStore.data.collect { prefs ->
                 _readToken.value = prefs[READ_TOKEN_KEY] ?: ""
                 _writeToken.value = prefs[WRITE_TOKEN_KEY] ?: ""
             }
         }
     }

    fun loadSims() {
        viewModelScope.launch {
            try {
                val token = _readToken.value
                if (token.isBlank()) {
                    Log.e("Simbase", "Token de lectura vacío")
                    return@launch
                }

                val response = ApiClient.api.getAllSims(token)
                _sims.value = response.simcards ?: emptyList()

            } catch (e: Exception) {
                Log.e("Simbase", "Error cargando SIMs", e)
            }
        }
    }

    fun toggleSim(sim: SimCard) {
        viewModelScope.launch {
            val newState = if (sim.state == "enabled") "disabled" else "enabled"

            try {
                val token = _writeToken.value
                if (token.isBlank()) {
                    Log.e("Simbase", "Token de escritura vacío")
                    return@launch
                }

                ApiClient.api.setSimState(
                    sim.iccid,
                    token,
                    SimStateRequest(newState)
                )

                // Recarga inmediata
                loadSims()

                // Delay según operación
                val delayMs = if (newState == "disabled") 11_000 else 17_000
                delay(delayMs.toLong())

                // Recarga final
                loadSims()

            } catch (e: Exception) {
                Log.e("Simbase", "Error modificando SIM", e)
            }
        }
    }

     fun updateReadToken(value: String) {
         _readToken.value = value
     }

     fun updateWriteToken(value: String) {

         _writeToken.value = value
     }

     fun saveTokens() {

         Log.d("VM", "Guardando tokens desde ViewModel")
         viewModelScope.launch {
             dataStore.edit { prefs ->
                 prefs[READ_TOKEN_KEY] = _readToken.value
                 prefs[WRITE_TOKEN_KEY] = _writeToken.value
             }
         }
     }

     fun clearTokens(onCleared: () -> Unit) {
         viewModelScope.launch {
             dataStore.edit { prefs ->
                 prefs[READ_TOKEN_KEY] = ""
                 prefs[WRITE_TOKEN_KEY] = ""
             }
             Log.d("CLEAR", "Borrando tokens…")
             onCleared()   // ← solo se ejecuta cuando DataStore ha terminado
         }
     }
     private val _selectedSim = MutableStateFlow<SimCard?>(null)
     val selectedSim = _selectedSim.asStateFlow()

     fun selectSim(sim: SimCard) {
         _selectedSim.value = sim
     }

     fun clearSelectedSim() {
         _selectedSim.value = null
     }
}