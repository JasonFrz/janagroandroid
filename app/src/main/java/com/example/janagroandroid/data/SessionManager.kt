package com.example.janagroandroid.data

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("jan_agro_session", Context.MODE_PRIVATE)
    fun saveLogin(userId: Int) = prefs.edit().putInt("user_id", userId).apply()
    fun getUserId(): Int = prefs.getInt("user_id", -1)
    fun isLoggedIn(): Boolean = getUserId() != -1
    fun logout() = prefs.edit().clear().apply()
}
