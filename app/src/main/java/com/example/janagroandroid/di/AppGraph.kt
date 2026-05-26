package com.example.janagroandroid.di

import android.content.Context
import com.example.janagroandroid.data.local.AppDatabase
import com.example.janagroandroid.data.local.SessionManager
import com.example.janagroandroid.data.remote.RetrofitClient
import com.example.janagroandroid.data.repository.AppRepository

object AppGraph {
    fun repository(context: Context): AppRepository {
        val db = AppDatabase.getInstance(context)
        val sessionManager = SessionManager(context)
        return AppRepository(
            userDao = db.userDao(),
            productDao = db.productDao(),
            cartDao = db.cartDao(),
            historyDao = db.historyDao(),
            apiService = RetrofitClient.getApiService(sessionManager),
            sessionManager = sessionManager
        )
    }
}