package com.xabi.simbase.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xabi.simbase.data.TokenStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val context: Context) : ViewModel() {

    private val _readToken = MutableStateFlow("")
    val readToken = _readToken.asStateFlow()

    private val _writeToken = MutableStateFlow("")
    val writeToken = _writeToken.asStateFlow()

    init {
        viewModelScope.launch {
            TokenStore.readTokens(context).collect { (read, write) ->
                _readToken.value = read ?: ""
                _writeToken.value = write ?: ""
            }
        }
    }

    fun updateReadToken(value: String) {
        _readToken.value = value
    }

    fun updateWriteToken(value: String) {
        _writeToken.value = value
    }

    private val _saved = MutableStateFlow(false)
    val saved = _saved

    fun saveTokens() {
        viewModelScope.launch {
            TokenStore.saveTokens(context, readToken.value, writeToken.value)
        }
    }
}