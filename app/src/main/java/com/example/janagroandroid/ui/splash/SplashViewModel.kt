package com.example.janagroandroid.ui.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.janagroandroid.data.repository.AppRepository

class SplashViewModel(app: Application, private val repo: AppRepository) : AndroidViewModel(app) {
    fun isLoggedIn(): Boolean = repo.isLoggedIn()

    suspend fun getRole(): String {
        return repo.getCurrentUser()?.role ?: "Customer"
    }
}