package com.example.janagroandroid.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val repo: AppRepository
) : ViewModel() {

    private val _startDestination = MutableStateFlow("login")
    val startDestination: StateFlow<String> = _startDestination.asStateFlow()

    fun checkSession() {
        viewModelScope.launch {
            _startDestination.value = if (repo.isLoggedIn && repo.getCurrentUser() != null) {
                "home"
            } else {
                "login"
            }
        }
    }

    suspend fun getRole(): String {
        return repo.getCurrentUser()?.role ?: "Customer"
    }
}