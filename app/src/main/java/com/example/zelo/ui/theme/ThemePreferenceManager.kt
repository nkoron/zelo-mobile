package com.example.zelo.ui.theme

import android.content.Context
import android.content.SharedPreferences

class ThemePreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val DARK_MODE_KEY = "dark_mode"
    }

    fun isDarkModeEnabled(): Boolean {
        return sharedPreferences.getBoolean(DARK_MODE_KEY, false)
    }

    fun setDarkModeEnabled(isEnabled: Boolean) {
        sharedPreferences.edit().putBoolean(DARK_MODE_KEY, isEnabled).apply()
    }
}
