package com.example.janagroandroid.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.remote.dto.AdminUserDto
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class AdminUsersViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {

    private val _users = MutableLiveData<List<AdminUserDto>>(emptyList())
    val users: LiveData<List<AdminUserDto>> = _users

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Cache semua user dari server; filtering dilakukan lokal.
    private var allUsers: List<AdminUserDto> = emptyList()
    private var searchQuery: String = ""
    private var roleFilter: String? = null

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            allUsers = repo.getAdminUsers()
            applyFilters()
            _isLoading.postValue(false)
        }
    }

    fun setSearch(query: String) {
        searchQuery = query
        applyFilters()
    }

    fun setRoleFilter(role: String?) {
        roleFilter = role
        applyFilters()
    }

    private fun applyFilters() {
        val query = searchQuery.trim()
        val role = roleFilter
        val filtered = allUsers.filter { user ->
            val matchesRole = role == null || user.role.equals(role, ignoreCase = true)
            val matchesSearch = query.isBlank() ||
                user.name.contains(query, ignoreCase = true) ||
                user.email.contains(query, ignoreCase = true)
            matchesRole && matchesSearch
        }
        _users.postValue(filtered)
    }
}