package com.example.sinope.data.app_manager

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.sinope.core.utils.Constants
import com.example.sinope.domain.repository.app_manager.ILocalUserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


class LocalUserPreferencesImpl(
    private val context : Context
): ILocalUserPreferences {

    override suspend fun saveAppEntry() {
        Log.d("LocalUserPreferencesImpl", "saveAppEntry: ${context.dataStore.data.first()[PreferenceKeys.APP_ENTRY]}")
        context.dataStore.edit { settings ->
            settings[PreferenceKeys.APP_ENTRY] = true
        }
    }

    override fun readAppEntry(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferenceKeys.APP_ENTRY] ?: false
        }
    }

    override suspend fun saveLanguage(language: String) {
        context.dataStore.edit { settings ->
            settings[PreferenceKeys.LANGUAGE] = language
        }
    }

    override fun readLanguage(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferenceKeys.LANGUAGE] ?: "SYSTEM"
        }
    }
}

private val readOnlyProperty = preferencesDataStore(name = Constants.USER_SETTINGS)
val Context.dataStore: DataStore<Preferences> by readOnlyProperty

private object PreferenceKeys {
    val APP_ENTRY = booleanPreferencesKey(Constants.APP_ENTRY)
    val LANGUAGE = stringPreferencesKey(Constants.LANGUAGE)
}
