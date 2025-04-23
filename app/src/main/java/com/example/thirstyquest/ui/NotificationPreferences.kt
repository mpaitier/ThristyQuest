package com.example.thirstyquest.ui

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore

import kotlinx.coroutines.flow.first


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
object NotificationPreferences {
    private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
    suspend fun getNotificationsEnabled(context: Context): Boolean {
        val preferences = context.dataStore.data.first()
        val isEnabled = preferences[NOTIFICATIONS_ENABLED_KEY] ?: true

        val permissionGranted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        Log.d("DataStore", "Appel à getNotificationsEnabled depuis : ${Thread.currentThread().name}")

        return isEnabled && permissionGranted
    }


    suspend fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED_KEY] = enabled
        }
    }
}