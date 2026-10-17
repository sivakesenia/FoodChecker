package com.example.foodchecker.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

data class AppSettings(
    val registryVersion: Int,
    val isDemoDataSeeded: Boolean
)

class AppSettingsDataSource(
    private val dataStore: DataStore<Preferences>
) {

    val settings: Flow<AppSettings> = dataStore.data.map { preferences ->
        AppSettings(
            registryVersion = preferences[REGISTRY_VERSION] ?: 0,
            isDemoDataSeeded = preferences[DEMO_DATA_SEEDED] ?: false
        )
    }

    suspend fun current(): AppSettings = settings.first()

    suspend fun setRegistryVersion(version: Int) {
        dataStore.edit { it[REGISTRY_VERSION] = version }
    }

    suspend fun setDemoDataSeeded(seeded: Boolean) {
        dataStore.edit { it[DEMO_DATA_SEEDED] = seeded }
    }

    private companion object {
        val REGISTRY_VERSION = intPreferencesKey("registry_version")
        val DEMO_DATA_SEEDED = booleanPreferencesKey("demo_data_seeded")
    }
}
