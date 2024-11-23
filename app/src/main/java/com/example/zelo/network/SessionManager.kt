package com.example.zelo.network

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class SessionManager(context: Context) {
    private var preferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    fun loadAuthToken(): String? {
        return preferences.getString(AUTH_TOKEN, null)
    }
    fun saveAuthToken(token: String) {
        val editor = preferences.edit()
        editor.putString(AUTH_TOKEN, token)
        editor.apply()
    }
    fun removeAuthToken(){
        val editor = preferences.edit()
        editor.remove(AUTH_TOKEN)
        editor.apply()
    }
    fun logout(){
        _logoutSignal.tryEmit(Unit)
    }
    private val _logoutSignal = MutableSharedFlow<Unit>(replay= 0)
    val logoutSignal : SharedFlow<Unit> get() = _logoutSignal

    companion object{
        const val PREFERENCES_NAME = "MyPrefs"
        const val AUTH_TOKEN = "auth_token"
    }
}