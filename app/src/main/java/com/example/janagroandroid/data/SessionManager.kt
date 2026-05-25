package com.example.janagroandroid.data

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("jan_agro_session", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    fun getToken(): String {
        return prefs.getString("token", "") ?: ""
    }

    fun saveUserId(userId: Long) {
        prefs.edit().putLong("user_id", userId).apply()
    }

    fun getUserId(): Long {
        return prefs.getLong("user_id", -1L)
    }

    fun isLoggedIn(): Boolean {
        return getToken().isNotBlank()
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}