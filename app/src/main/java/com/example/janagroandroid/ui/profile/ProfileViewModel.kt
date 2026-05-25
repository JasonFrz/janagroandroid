package com.example.janagroandroid.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.local.entity.UserEntity
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repo: AppRepository
) : ViewModel() {

    val user: LiveData<UserEntity?> = repo.getUser

    fun logout(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repo.logout()
            onDone()
        }
    }
}