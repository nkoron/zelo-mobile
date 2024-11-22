package com.example.zelo.ui.theme


import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val themePreferences = ThemePreferences(application)

    private val _isDarkTheme = mutableStateOf(themePreferences.isDarkModeEnabled())

    fun toggleTheme() {
        val newTheme = !_isDarkTheme.value
        _isDarkTheme.value = newTheme

        // Guarda el nuevo estado en SharedPreferences
        viewModelScope.launch {
            themePreferences.setDarkModeEnabled(newTheme)
        }
    }
}

