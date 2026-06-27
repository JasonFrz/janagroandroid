package com.example.janagroandroid.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("jan_agro_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val USER_TOKEN = "user_token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val USER_ID = "user_id"
        private const val USER_ROLE = "user_role"
    }

    fun saveToken(token: String) {
        prefs.edit().putString(USER_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun saveRefreshToken(refreshToken: String) {
        prefs.edit().putString(REFRESH_TOKEN, refreshToken).apply()
    }

    fun getRefreshToken(): String? {
        return prefs.getString(REFRESH_TOKEN, null)
    }

    fun saveUserId(id: Long) {
        prefs.edit().putLong(USER_ID, id).apply()
    }

    fun getUserId(): Long {
        return prefs.getLong(USER_ID, -1)
    }

    fun saveUserRole(role: String) {
        prefs.edit().putString(USER_ROLE, role).apply()
    }

    fun getUserRole(): String? {
        return prefs.getString(USER_ROLE, null)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun saveAiTips(productId: Long, tips: String) {
        prefs.edit().putString("ai_tips_$productId", tips).apply()
    }

    fun getAiTips(productId: Long): String? {
        return prefs.getString("ai_tips_$productId", null)
    }
}
