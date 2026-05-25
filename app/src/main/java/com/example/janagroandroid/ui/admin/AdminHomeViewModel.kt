package com.example.janagroandroid.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.remote.dto.AdminStats
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminHomeViewModel(
    private val repo: AppRepository
) : ViewModel() {

    private val _stats = MutableStateFlow<AdminStats?>(null)
    val stats: StateFlow<AdminStats?> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadStats() {
        viewModelScope.launch {
            _isLoading.value = true
            _stats.value = repo.getAdminStats()
            _isLoading.value = false
        }
    }
}