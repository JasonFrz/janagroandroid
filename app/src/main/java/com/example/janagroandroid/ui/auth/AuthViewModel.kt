package com.example.janagroandroid.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.local.entity.UserEntity
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val message: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val success = repo.login(email, password)
            if (success) {
                _uiState.value = AuthUiState.Success("Login successful")
                onResult(true)
            } else {
                _uiState.value = AuthUiState.Error("Invalid credentials")
                onResult(false)
            }
        }
    }

    fun register(
        name: String, username: String, email: String, password: String,
        dob: String, gender: String, role: String, onResult: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val success = repo.register(
                UserEntity(
                    name = name,
                    username = username,
                    email = email,
                    password = password,
                    dateOfBirth = dob,
                    gender = gender,
                    role = role
                )
            )
            if (success) {
                _uiState.value = AuthUiState.Success("Registration successful")
                onResult(true)
            } else {
                _uiState.value = AuthUiState.Error("Registration failed")
                onResult(false)
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
