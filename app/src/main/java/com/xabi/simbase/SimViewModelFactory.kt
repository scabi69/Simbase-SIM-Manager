package com.xabi.simbase

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

class SimViewModelFactory(
    private val dataStore: DataStore<Preferences>,
    private val context: Context? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SimViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SimViewModel(dataStore, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}