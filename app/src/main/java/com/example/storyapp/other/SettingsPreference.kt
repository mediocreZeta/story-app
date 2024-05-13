package com.example.storyapp.other

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsPreference(private val dataStore: DataStore<Preferences>) {
    private val TOKEN_KEY = stringPreferencesKey("token_key")
    private val IS_SESSION_AVAILABLE = booleanPreferencesKey("session_status")

    fun getTokenKey(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    fun getSessionAvailability(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_SESSION_AVAILABLE] ?: false
        }
    }

    suspend fun saveTokenKey(tokenKey: String) {
        dataStore.edit { settings ->
            settings[TOKEN_KEY] = tokenKey
        }
    }

    suspend fun updateSessionStatusTo(status: Boolean) {
        dataStore.edit { settings ->
            settings[IS_SESSION_AVAILABLE] = status
        }
    }

    suspend fun resetAllValue() {
        dataStore.edit { settings ->
            settings[TOKEN_KEY] = ""
            settings[IS_SESSION_AVAILABLE] = false
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingsPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingsPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingsPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}