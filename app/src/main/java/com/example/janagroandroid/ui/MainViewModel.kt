package com.example.janagroandroid.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.janagroandroid.data.local.entity.UserEntity
import com.example.janagroandroid.data.repository.AppRepository

class MainViewModel(
    private val repository: AppRepository
) : ViewModel() {

    val user: LiveData<UserEntity?> = repository.getUser
    val isLoggedIn: Boolean
        get() = repository.isLoggedIn
}