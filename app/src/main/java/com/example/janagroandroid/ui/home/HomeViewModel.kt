package com.example.janagroandroid.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.local.entity.ProductEntity
import com.example.janagroandroid.data.local.entity.UserEntity
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repo: AppRepository
) : ViewModel() {

    val products: LiveData<List<ProductEntity>> = repo.products
    val user: LiveData<UserEntity?> = repo.getUser

    fun refreshRemoteProducts() {
        viewModelScope.launch {
            repo.refreshRemoteProducts()
        }
    }
}