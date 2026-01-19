package com.xabi.simbase.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

val Context.dataStore by preferencesDataStore("simbase_prefs")

object TokenStore {

    private val READ_TOKEN = stringPreferencesKey("read_token")
    private val WRITE_TOKEN = stringPreferencesKey("write_token")

    fun readTokens(context: Context): Flow<Pair<String, String>> =
        context.dataStore.data.map { prefs ->
            val read = prefs[READ_TOKEN] ?: ""
            val write = prefs[WRITE_TOKEN] ?: ""
            Pair(read, write)
        }

    suspend fun saveReadToken(context: Context, token: String) {
        context.dataStore.edit { it[READ_TOKEN] = token }
    }

    suspend fun saveWriteToken(context: Context, token: String) {
        context.dataStore.edit { it[WRITE_TOKEN] = token }
    }

    suspend fun saveTokens(context: Context, read: String, write: String) {
        context.dataStore.edit { prefs ->
            prefs[READ_TOKEN] = read
            prefs[WRITE_TOKEN] = write
        }
    }
}