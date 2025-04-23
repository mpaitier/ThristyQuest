package com.example.thirstyquest.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewModelScope
import com.example.thirstyquest.ui.dataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch



class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    private val context = application.applicationContext

    private val _isDarkMode = MutableStateFlow<Boolean?>(null)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.filterNotNull().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    init {
        viewModelScope.launch {
            context.dataStore.data
                .map { preferences -> preferences[DARK_MODE_KEY] }
                .collect { storedValue ->
                    _isDarkMode.value = storedValue
                }
        }
    }

    fun initializeDefaultIfNeeded(systemDark: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                if (!prefs.contains(DARK_MODE_KEY)) {
                    prefs[DARK_MODE_KEY] = systemDark
                    _isDarkMode.value = systemDark
                }
            }
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                val newValue = !(_isDarkMode.value ?: false)
                prefs[DARK_MODE_KEY] = newValue
                _isDarkMode.value = newValue
            }
        }
    }
}
