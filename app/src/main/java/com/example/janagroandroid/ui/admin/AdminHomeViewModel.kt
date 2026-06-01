package com.example.janagroandroid.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.remote.dto.AdminStats
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class AdminHomeViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {

    private val _stats = MutableLiveData<AdminStats?>()
    val stats: LiveData<AdminStats?> = _stats

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadStats() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            val result = repo.getAdminStats()
            _stats.postValue(result)
            _isLoading.postValue(false)
        }
    }
}